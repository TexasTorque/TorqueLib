/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * West Coast Products Swerve X Module.
 *
 * @author Justus Languell - Omar Afzal
 */
public final class TorqueSwerveX extends TorqueSwerveModule {

    /**
     * A structure to define the constants for the swerve module. It has default values that can be
     * overriden before written to the module.
     */
    public static final class SwerveConfig {
        public static final SwerveConfig defaultConfig = new SwerveConfig();

        public int driveMaxCurrent = 35, turnMaxCurrent = 25;

        public double voltageCompensation = 12.6,

                maxVelocity = 5, maxAcceleration = 9.6, maxAngularVelocity = 2 * Math.PI,
                maxAngularAcceleration = 2 * Math.PI,

                drivePGain = 0.2, driveIGain = 0.0, driveDGain = 0.0, driveStaticGain = 0.015,
                driveFeedForward = 0.212,

                driveGearRatio = 6.57, wheelDiameter = 0.1016,
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI),
                drivePosFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI),

                turnPGain = 0.5, turnIGain = 0.0, turnDGain = 0.0, turnGearRatio = 12.41;

    }

    public static final class SwervePorts {
        public final int drive, turn, encoder;

        public SwervePorts(final int drive, final int turn, final int encoder) {
            this.drive = drive;
            this.turn = turn;
            this.encoder = encoder;
        }
    }

    /**
     * Normalizes drive speeds to never exceed a specified max.
     *
     * @param states The swerve module states
     * @param max Maximum translational speed.
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

    private final SwerveConfig config;

    private final TorqueNEO drive, turn;

    private final CANCoder cancoder;

    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFeedForward;

    public final double staticOffset;

    public final String name;

    public TorqueSwerveX(final String name, final SwervePorts ports, final double staticOffset,
            final SwerveConfig config) {
        super(ports.drive);
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.staticOffset = staticOffset;
        this.config = config;

        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(config.driveMaxCurrent);
        drive.setVoltageCompensation(config.voltageCompensation);
        drive.setBreakMode(true);
        drive.invertMotor(false);
        drive.setConversionFactors(config.drivePosFactor, config.driveVelocityFactor);
        drive.burnFlash();

        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(config.turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(config.turnMaxCurrent);
        turn.setVoltageCompensation(config.voltageCompensation);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANCoder(ports.encoder);
        final CANCoderConfiguration cancoderConfig = new CANCoderConfiguration();
        cancoderConfig.sensorCoefficient = 2 * Math.PI / 4096.0;
        cancoderConfig.unitString = "rad";
        cancoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
        cancoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        cancoder.configAllSettings(cancoderConfig);

        drivePID = new PIDController(config.drivePGain, config.driveIGain, config.driveDGain);
        turnPID = new PIDController(config.turnPGain, config.turnIGain, config.turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFeedForward =
                new SimpleMotorFeedforward(config.driveStaticGain, config.driveFeedForward);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        setDesiredState(state, DriverStation.isAutonomous());
    }

    public void setDesiredState(final SwerveModuleState state, final boolean useSmartDrive) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        if (useSmartDrive) {
            final double drivePIDOutput =
                    drivePID.calculate(drive.getVelocity(), optimized.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);
            drive.setPercent(drivePIDOutput + driveFFOutput);
        } else {
            drive.setPercent(optimized.speedMetersPerSecond / config.maxVelocity);
        }

        final double turnPIDOutput =
                turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        turn.setPercent(turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(drive.getPosition(), getRotation());
    }

    @Override
    public Rotation2d getRotation() {
        return Rotation2d.fromRadians(getTurnEncoder());
    }

    public void stop() {
        drive.setPercent(0.0);
        turn.setPercent(0.0);
    }

    private double getTurnEncoder() {
        return coterminal(cancoder.getPosition() - staticOffset);
    }

}
