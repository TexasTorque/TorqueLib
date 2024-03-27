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

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * WCP Swerve X modules with WCP/CRTE Krakens 🐙
 *
 */
public final class TorqueSwerveModuleKraken extends TorqueSwerveModule {

    // The NEO motor for turn.
    private final TorqueNEO turn;

    // The Kraken for drive.
    private final TalonFX drive;

    private final TalonFXConfiguration driveConfig;

    private final CurrentLimitsConfigs driveCurrentLimitsConfigs;

    // private final VoltageOut driveVoltageOut = new VoltageOut(0);
    private final DutyCycleOut drivePercentOutput = new DutyCycleOut(0);

    // The CANCoder for wheel angle measurement.
    private final CANcoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;

    private SimpleMotorFeedforward driveFeedForward;

    public TorqueSwerveModuleKraken(final String name, final SwervePorts ports, final SwerveConfig config) {
        super(name, config);

        // Configure the drive motor.
        // From 6328
        // (https://github.com/Mechanical-Advantage/RobotCode2024/blob/21bcc3bc1eacbf634cdd0a179c58c4561c8ec1e7/src/main/java/org/littletonrobotics/frc2024/subsystems/drive/ModuleIOKrakenFOC.java#L73)
        drive = new TalonFX(ports.drive);
        driveCurrentLimitsConfigs = new CurrentLimitsConfigs()
                .withStatorCurrentLimit(config.driveMaxCurrentStator)
                .withStatorCurrentLimitEnable(true)
                .withSupplyCurrentLimit(config.driveMaxCurrentSupply)
                .withSupplyCurrentLimitEnable(true);

        driveConfig = new TalonFXConfiguration();
        // driveConfig.TorqueCurrent.PeakForwardTorqueCurrent = 80.0; // maybe should
        // leave as default? (6328 had it)
        // driveConfig.TorqueCurrent.PeakReverseTorqueCurrent = -80.0;
        // driveConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod = 0.02;

        driveConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        driveConfig.Feedback.SensorToMechanismRatio = 1 / config.drivePoseFactor;

        // drive.optimizeBusUtilization(1.0); // this is kinda weird
        drive.getConfigurator().apply(driveConfig.withCurrentLimits(driveCurrentLimitsConfigs));

        drivePID = new PIDController(config.drivePGain, config.driveIGain, config.driveDGain);
        driveFeedForward = new SimpleMotorFeedforward(config.driveStaticGain, config.driveFeedForward);

        // Configure the turn motor.
        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(config.turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(config.turnMaxCurrent);
        turn.setVoltageCompensation(config.voltageCompensation);
        turn.setBreakMode(true);
        turn.burnFlash();

        // Configure the controllers
        turnPID = new PIDController(config.turnPGain, config.turnIGain, config.turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);

        cancoder = new CANcoder(ports.encoder);

        SmartDashboard.putNumber("Drive P", .1);
        SmartDashboard.putNumber("Drive FF", config.driveFeedForward);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        setDesiredState(state, DriverStation.isAutonomous());
    }

    private SwerveModulePosition aggregatePosition = new SwerveModulePosition(0, Rotation2d.fromRadians(0));
    private double lastSampledTime = -1;

    public void setDesiredState(final SwerveModuleState state, final boolean useSmartDrive) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        // Calculate drive output
        if (useSmartDrive) {
            final double drivePIDOutput = drivePID.calculate(drive.getVelocity().getValueAsDouble(),
                    optimized.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);

            // drive.setControl(drivePercentOutput.withOutput(drivePIDOutput +
            // driveFFOutput)); // control PID with percent
            drive.setControl(drivePercentOutput.withOutput(drivePIDOutput + driveFFOutput)); // control PID with percent
        } else {
            drive.setControl(drivePercentOutput.withOutput(optimized.speedMetersPerSecond / config.maxVelocity));
        }

        Debug.log(name + "Drive Velocity Actual", -drive.getVelocity().getValueAsDouble());
        Debug.log(name + "Drive Velocity Requested", -optimized.speedMetersPerSecond);
        Debug.log("4.6", 4.6);

        // drivePID.setP(SmartDashboard.getNumber("Drive P", 0));
        // driveFeedForward = new SimpleMotorFeedforward(config.driveStaticGain,
        // SmartDashboard.getNumber("Drive FF", 0));

        // Calculate turn output
        final double turnPIDOutput = -turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        turn.setPercent(turnPIDOutput);

        // For simulator:
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
        return new SwerveModuleState(drive.getVelocity().getValueAsDouble(), getRotation());
    }

    public SwerveModulePosition getPosition() {
        if (!RobotBase.isReal())
            return aggregatePosition;
        else
            return new SwerveModulePosition(drive.getPosition().getValueAsDouble(), getRotation());
    }

    @Override
    public Rotation2d getRotation() {
        return Rotation2d.fromRadians(getTurnEncoder());
    }

    private double getTurnEncoder() {
        return MathUtil.angleModulus(Math.toRadians(cancoder.getAbsolutePosition().getValue() * 360));
    }
}
