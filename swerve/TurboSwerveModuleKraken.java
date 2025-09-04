package org.texastorque.torquelib.swerve;

import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.motors.TorqueKraken;
import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

/**
 * 7492 Turbo Torque 
 * Double Kraken Swerve Module
 * 
 * @author Maaz Kattangere
 */
public final class TurboSwerveModuleKraken extends TorqueSwerveModule {

   
    /**
     * Normalizes drive speeds to never exceed a specified max.
     *
     * @param states The swerve module states, this is mutated!
     * @param max    Maximum translational speed.
     */
    public static void normalize(SwerveModuleState[] states, final double max) {
        double top = 0, buff;
        for (final SwerveModuleState state : states)
            if ((buff = (state.speedMetersPerSecond / max)) > top)
                top = buff;
        if (top != 0)
            for (SwerveModuleState state : states)
                state.speedMetersPerSecond /= top;
    }

    // The Kraken motors for drive and turn.
    private final TalonFX drive, turn;

    // The CANCoder for wheel angle measurement.
    private final CANcoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFeedForward;

    public boolean useCancoder = true;

    private final DutyCycleOut driveDutyCycle = new DutyCycleOut(0);

    public static final double maxVelocity = 5.0;

    public TurboSwerveModuleKraken(final String name, final SwervePorts ports, final boolean inverted) {
        super(name);

        // Configure the drive motor.
        final TalonFXConfiguration driveConfig = new TalonFXConfiguration();

        driveConfig.MotorOutput.Inverted = inverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
        driveConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        /* Gear Ratio Config */
        driveConfig.Feedback.SensorToMechanismRatio = 6.75;
        driveConfig.Feedback.RotorToSensorRatio = 1;

        /* Current Limiting */
        driveConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        driveConfig.CurrentLimits.SupplyCurrentLimit = 40;
        driveConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        driveConfig.CurrentLimits.StatorCurrentLimit = 80;

        /* Open and Closed Loop Ramping */
        driveConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = 0.0; // 0.25?
        driveConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = 0.0;
        driveConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = 0.0;
        driveConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = 0.0;

        drive = new TalonFX(ports.drive);
        drive.getConfigurator().apply(driveConfig);
        drive.getConfigurator().setPosition(0.0);


        // Configure the turn motor.
        final TalonFXConfiguration turnConfig = new TalonFXConfiguration();

        turnConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        turnConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        /* Gear Ratio Config */
        turnConfig.Feedback.SensorToMechanismRatio = 6.75;
        turnConfig.Feedback.RotorToSensorRatio = 1;

        /* Current Limiting */
        turnConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        turnConfig.CurrentLimits.SupplyCurrentLimit = 40;
        turnConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        turnConfig.CurrentLimits.StatorCurrentLimit = 80;

        /* Open and Closed Loop Ramping */
        turnConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = 0.0; // 0.25?
        turnConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = 0.0;
        turnConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = 0.0;
        turnConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = 0.0;

        turn = new TalonFX(ports.turn);
        turn.getConfigurator().apply(turnConfig);
        turn.getConfigurator().setPosition(0.0);

        // Setup encoder
        cancoder = new CANcoder(ports.encoder);

        // PID settings
        final double turnPGain = 0.375, turnIGain = 0.0, turnDGain = 0.0;
        final double driveStaticGain = 0.015, driveFFGain = 0.2485, drivePGain = 0.1, driveIGain = 0.0,
                driveDGain = 0.0;

        // Configure the controllers
        drivePID = new PIDController(drivePGain, driveIGain, driveDGain);
        turnPID = new PIDController(turnPGain, turnIGain, turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFeedForward = new SimpleMotorFeedforward(driveStaticGain, driveFFGain);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        setDesiredState(state, DriverStation.isAutonomous());
    }

    private SwerveModulePosition aggregatePosition = new SwerveModulePosition(0, Rotation2d.fromRadians(0));
    private double lastSampledTime = -1;

    public void setDesiredState(final SwerveModuleState state, final boolean useSmartDrive) {
        state.optimize(getRotation());

        final double driveVelocity = RPSToMPS(drive.getVelocity().getValueAsDouble());

        // Calculate drive output
        if (useSmartDrive) {
            final double drivePIDOutput = drivePID.calculate(driveVelocity, state.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(state.speedMetersPerSecond);
            final double driveOutput = drivePIDOutput + driveFFOutput;
            driveDutyCycle.Output = driveOutput;
        } else {
            driveDutyCycle.Output = state.speedMetersPerSecond / maxVelocity;
        }

        // Debug.log(name + " % Output", driveDutyCycle.Output);

        drive.setControl(driveDutyCycle);

        // Debug.log(name + " Real Velocity", Math.abs(driveVelocity));
        // Debug.log(name + " Req Velocity", state.speedMetersPerSecond);
        // Debug.log("Max Velocity", maxVelocity);

        // Calculate turn output
        final double turnPIDOutput = -turnPID.calculate(getTurnEncoder(), state.angle.getRadians());
        turn.set(turnPIDOutput);
        Debug.log(name+" Turn Encoder", getTurnCancoder()*(180/Math.PI));

        // Debug:
        if (!RobotBase.isReal()) {
            double time = Timer.getFPGATimestamp();
            if (lastSampledTime == -1)
                lastSampledTime = time;
            double deltaTime = time - lastSampledTime;
            lastSampledTime = time;
            aggregatePosition.distanceMeters += state.speedMetersPerSecond * deltaTime;
            aggregatePosition.angle = state.angle;
        }
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(RPSToMPS(drive.getVelocity().getValueAsDouble()), getRotation());
    }

    @Override
    public SwerveModulePosition getPosition() {
        if (!RobotBase.isReal()) {
            return aggregatePosition;
        }
        return new SwerveModulePosition(
            rotationsToMeters(drive.getPosition().getValueAsDouble()), 
            getRotation()
        );
    }

    @Override
    public Rotation2d getRotation() {
        return Rotation2d.fromRadians(getTurnEncoder());
    }

    public void stop() {
        // drive.setPercent(0.0);
        turn.set(0);
    }

    public void zero() {
        turn.set(turnPID.calculate(getTurnEncoder(), 0));
    }

    private double getTurnEncoder() {
        return getTurnCancoder();
    }

    private double getTurnCancoder() {
        // constrains to -Math.PI to Math.PI
        double absAngle = Math.toRadians(cancoder.getAbsolutePosition().getValueAsDouble() * 360);
        if (absAngle > Math.PI) {
            absAngle -= 2.0 * Math.PI;
        }
        return absAngle;
    }

    private static final double circumference = Units.inchesToMeters(4) * Math.PI;
    /**
     * @param wheelRPS Wheel Velocity: (in Rotations per Second)
     * @param circumference Wheel Circumference: (in Meters)
     * @return Wheel Velocity: (in Meters per Second)
     */
    public static double RPSToMPS(double wheelRPS) {
        return wheelRPS * circumference;
    }

    /**
     * @param wheelMPS Wheel Velocity: (in Meters per Second)
     * @param circumference Wheel Circumference: (in Meters)
     * @return Wheel Velocity: (in Rotations per Second)
     */
    public static double MPSToRPS(double wheelMPS) {
        return wheelMPS / circumference;
    }

    /**
     * @param wheelRotations Wheel Position: (in Rotations)
     * @param circumference Wheel Circumference: (in Meters)
     * @return Wheel Distance: (in Meters)
     */
    public static double rotationsToMeters(double wheelRotations) {
        return wheelRotations * circumference;
    }

    /**
     * @param wheelMeters Wheel Distance: (in Meters)
     * @param circumference Wheel Circumference: (in Meters)
     * @return Wheel Position: (in Rotations)
     */
    public static double metersToRotations(double wheelMeters) {
        return wheelMeters / circumference;
    }

}
