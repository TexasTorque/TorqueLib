/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

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
 * This is the version of the swerve module that uses a Kraken for driving. 
 * 
 * Super cool flipped swerve module built in 2023 by Abishek.
 * Updated to work with WCP Swerve X modules for 2024.
 * 
 * The module uses a WCP Kraken for drive and a Rev NEO for rotation control.
 * It uses a CTRE Cancoder positioned 1:1 with the wheel rotation.
 * 
 * Abishek Swerve:
 * https://drive.google.com/file/d/1bvgo75gqLzoerBIEtEObXep_6gpac6mZ/view?usp=sharing
 * https://drive.google.com/file/d/1q4cmx3Ntscfynn_1Tc8W9VAqR9Tgn0NT/view?usp=sharing
 *
 * The code is loosely based off the FRC 1706 swerve module.
 *
 * Cancoder docs:
 * https://api.ctr-electronics.com/phoenix/release/java/com/ctre/phoenix/sensors/CANCoder.html
 *
 * The rotation of the modules is [0, 2π) radians, with 0 being straight ahead.
 *
 * 0 or 2π
 * ↑
 * π/2 ← * → 3π/2
 * ↓
 * π
 *
 * @author Justus Languell
 */
public final class TorqueSwerveModuleKraken extends TorqueSwerveModule {

   
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

    private static double coterminal(final double rotation) {
        double coterminal = rotation;
        final double full = Math.signum(rotation) * 2 * Math.PI;
        while (coterminal > Math.PI || coterminal < -Math.PI)
            coterminal -= full;
        return coterminal;
    }

    // The Kraken motor for drive.
    private final TalonFX drive;

    // The NEO motors for turn.
    private final TorqueNEO turn;

    // The CANCoder for wheel angle measurement.
    private final CANcoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFeedForward;

    public boolean useCancoder = true;

    private final DutyCycleOut driveDutyCycle = new DutyCycleOut(0);

    public static final double maxVelocity = 5.0;

    public TorqueSwerveModuleKraken(final String name, final SwervePorts ports) {
        super(name);

        // Configure the drive motor.

        final TalonFXConfiguration driveConfig = new TalonFXConfiguration();


        driveConfig.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;
        driveConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        /* Gear Ratio Config */
        driveConfig.Feedback.SensorToMechanismRatio = 6.75;
        driveConfig.Feedback.RotorToSensorRatio = 1;

        /* Current Limiting */
        driveConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        driveConfig.CurrentLimits.SupplyCurrentLimit = 35;
        driveConfig.CurrentLimits.SupplyCurrentThreshold = 60;
        driveConfig.CurrentLimits.SupplyTimeThreshold = 0.1;
        driveConfig.CurrentLimits.StatorCurrentLimitEnable = true;
        driveConfig.CurrentLimits.StatorCurrentLimit = 50;

        /* Open and Closed Loop Ramping */
        driveConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = 0.0; // 0.25?
        driveConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = 0.0;
        driveConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = 0.0;
        driveConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = 0.0;

        drive = new TalonFX(ports.drive);
        drive.getConfigurator().apply(driveConfig);
        drive.getConfigurator().setPosition(0.0);


         // The following will most likely need to be overriden
        // depending on the weight of each robot
        final double driveStaticGain = 0.015, driveFFGain = 0.2485, drivePGain = 0.1, driveIGain = 0.0,
                driveDGain = 0.0;


        // Some turn parameters
        final double turnPGain = 0.375, turnIGain = 0.0, turnDGain = 0.0, 
                turnGearRatio = 468.0 / 35.0; // Rotation motor to wheel; // Rotation motor to wheel

        // Configure the turn motor.
        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(25);
        turn.setVoltageCompensation(12.6);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANcoder(ports.encoder);

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
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        final double driveVelocity = RPSToMPS(drive.getVelocity().getValueAsDouble());

        // Calculate drive output
        if (useSmartDrive) {

            final double drivePIDOutput = drivePID.calculate(driveVelocity, optimized.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);
            final double driveOutput = drivePIDOutput + driveFFOutput;
            driveDutyCycle.Output = driveOutput;
        } else {
            driveDutyCycle.Output = optimized.speedMetersPerSecond / maxVelocity;
        }

        Debug.log(name + " % Output", driveDutyCycle.Output);

        drive.setControl(driveDutyCycle);

        Debug.log(name + " Real Velocity", Math.abs(driveVelocity));
        Debug.log(name + " Req Velocity", optimized.speedMetersPerSecond);
        Debug.log("Max Velocity", maxVelocity);

        // Calculate turn output
        final double turnPIDOutput = -turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        turn.setPercent(turnPIDOutput);

        // Debug:
        if (!RobotBase.isReal()) {
            double time = Timer.getFPGATimestamp();
            if (lastSampledTime == -1)
                lastSampledTime = time;
            double deltaTime = time - lastSampledTime;
            lastSampledTime = time;
            aggregatePosition.distanceMeters += optimized.speedMetersPerSecond * deltaTime;
            aggregatePosition.angle = optimized.angle;
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
        turn.setPercent(0.0);
    }

    public void zero() {
        turn.setPercent(turnPID.calculate(getTurnEncoder(), 0));
    }

    private double getTurnEncoder() {
        return useCancoder ? getTurnCancoder() : getTurnNEOEncoder();
    }

    private double getTurnNEOEncoder() {
        return coterminal(turn.getPosition());
    }

    private double getTurnCancoder() {
        // Should not need to use Coterminal -- doing so anyways?
        double absAngle = Math.toRadians(cancoder.getAbsolutePosition().getValueAsDouble() * 360);
        absAngle %= 2.0 * Math.PI;
        if (absAngle < 0.0) {
            absAngle += 2.0 * Math.PI;
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
