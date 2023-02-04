/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderConfiguration;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.ctre.phoenix.sensors.SensorTimeBase;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.opencv.core.RotatedRect;
import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;

/**
 * Super cool flipped swerve module built in 2023 by Abishek.
 *
 * https://drive.google.com/file/d/1bvgo75gqLzoerBIEtEObXep_6gpac6mZ/view?usp=sharing
 * https://drive.google.com/file/d/1q4cmx3Ntscfynn_1Tc8W9VAqR9Tgn0NT/view?usp=sharing
 *
 * The module uses a Rev NEO for drive and rotation control. It uses a CTRE Cancoder
 * positioned 1:1 with the wheel rotation.
 *
 * The code is kinda based off the FRC 1706 swerve module.
 *
 * Cancoder docs: https://api.ctr-electronics.com/phoenix/release/java/com/ctre/phoenix/sensors/CANCoder.html
 *
 * The rotation of the modules is [0, 2π) radians, with 0 being straight ahead (I think).
 *
 *     0 or 2π
 *        ↑
 *  π/2 ← * → 3π/2
 *        ↓
 *        π
 *
 * @author Justus Languell
 */
public final class TorqueSwerveModule2022 extends TorqueSwerveModule {

    private final SwerveConfig config;

    // The NEO motors for turn and drive.
    private final TorqueNEO drive, turn;

    // The CANCoder for wheel angle measurement.
    private final CANCoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;
    private final SimpleMotorFeedforward driveFeedForward;

    // Rotation offset for tearing
    public final double staticOffset;

    // The name of the module that we can use for SmartDashboard outputs
    public final String name;

    public boolean useSmartDrive = false;

    public TorqueSwerveModule2022(final String name, final SwervePorts ports, final double staticOffset,
                                  final SwerveConfig config) {
        this(name, ports.drive, ports.turn, ports.encoder, staticOffset, config);
    }

    public TorqueSwerveModule2022(final String name, final int driveID, final int turnID, final int encoderID,
                                  final double staticOffset, final SwerveConfig config) {
        super(driveID);
        this.name = name.replaceAll(" ", "_").toLowerCase();
        this.staticOffset = staticOffset;
        this.config = config;

        // Configure the drive motor.
        drive = new TorqueNEO(driveID);
        drive.setCurrentLimit(config.driveMaxCurrent);
        drive.setVoltageCompensation(config.voltageCompensation);
        drive.setBreakMode(true);
        drive.setConversionFactors(config.drivePoseFactor, config.driveVelocityFactor);
        drive.burnFlash();

        // Configure the turn motor.
        turn = new TorqueNEO(turnID);
        turn.setConversionFactors(config.turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(config.turnMaxCurrent);
        turn.setVoltageCompensation(config.voltageCompensation);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANCoder(encoderID);
        final CANCoderConfiguration cancoderConfig = new CANCoderConfiguration();
        cancoderConfig.sensorCoefficient = 2 * Math.PI / 4096.0;
        cancoderConfig.unitString = "rad";
        cancoderConfig.sensorTimeBase = SensorTimeBase.PerSecond;
        // cancoderConfig.initializationStrategy = SensorInitializationStrategy.BootToZero;
        cancoderConfig.initializationStrategy = SensorInitializationStrategy.BootToAbsolutePosition;
        cancoder.configAllSettings(cancoderConfig);

        // Configure the controllers
        drivePID = new PIDController(config.drivePGain, config.driveIGain, config.driveDGain);
        turnPID = new PIDController(config.turnPGain, config.turnIGain, config.turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFeedForward = new SimpleMotorFeedforward(config.driveStaticGain, config.driveFeedForward);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        // Calculate drive output
        if (useSmartDrive) {
            final double drivePIDOutput = drivePID.calculate(drive.getVelocity(), optimized.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(optimized.speedMetersPerSecond);
            log("Drive PID Output", drivePIDOutput + driveFFOutput);
            drive.setPercent(drivePIDOutput + driveFFOutput);
        } else {
            drive.setPercent(optimized.speedMetersPerSecond / config.maxVelocity);
        }

        // Calculate turn output
        final double turnPIDOutput = turnPID.calculate(getTurnEncoder(), optimized.angle.getRadians());
        log("Turn PID Output", turnPIDOutput);
        turn.setPercent(turnPIDOutput);
    }

    @Override
    public SwerveModuleState getState() {
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    public SwerveModulePosition getPosition() { return new SwerveModulePosition(drive.getPosition(), getRotation()); }

    @Override
    public Rotation2d getRotation() {
        return new Rotation2d(getTurnEncoder());
    }

    private double getTurnEncoder() { return useCancoder ? getTurnCancoder() : getTurnNEOEncoder(); }

    private double getTurnNEOEncoder() { return coterminal(turn.getPosition()); }

    private double getTurnCancoder() {
        // Should not need to use Coterminal
        // return log("cancoder", coterminal(cancoder.getPosition()) - staticOffset);
        // return log("cancoder", coterminal(cancoder.getPosition()));
        // return log("cancoder", coterminal(cancoder.getPosition()) - staticOffset);
        return log("cancoder", coterminal(cancoder.getPosition() - staticOffset));
    }

    public void stop() {
        drive.setPercent(0.0);
        turn.setPercent(0.0);
    }

    public void zero() { turn.setPercent(log("zero pid", turnPID.calculate(getTurnEncoder(), 0))); }

    private double log(final String item, final double value) {
        final String key = name + "." + item.replaceAll(" ", "_").toLowerCase();
        SmartDashboard.putNumber(String.format("%s::%s", name, key), value);
        return value;
    }

    public boolean useCancoder = true;

    /**
     * A structure to define the constants for the swerve module.
     *
     * Has default values that can be overriden before written to
     * the module.
     */
    public static final class SwerveConfig {
        public static final SwerveConfig defaultConfig = new SwerveConfig();

        public double magic = 6.57 / (8.0 + 1.0 / 3.0);

        public int driveMaxCurrent = 35,          // amps
                turnMaxCurrent = 25;              // amps
        public double voltageCompensation = 12.6, // volts
                maxVelocity = 3.25,               // m/s
                maxAcceleration = 3.0,            // m/s^2
                maxAngularVelocity = Math.PI,     // radians/s
                maxAngularAcceleration = Math.PI, // radians/s

                // The following will most likely need to be overriden
                // depending on the weight of each robot
                driveStaticGain = 0.015, driveFeedForward = 0.212, drivePGain = 0.2, driveIGain = 0.0, driveDGain = 0.0,

                      driveRampRate = 3.0,    // %power/s
                driveGearRatio = 6.57,        // Translation motor to wheel
                wheelDiameter = 4.0 * 0.0254, // m
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI), // m/s
                drivePoseFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI),            // m
                turnPGain = 0.5, turnIGain = 0.0, turnDGain = 0.0,
                      turnGearRatio = 12.41; // Rotation motor to wheel
    }

    /**
     * Normalizes drive speeds to never exceed a specified max.
     *
     * @param states The swerve module states, this is mutated!
     * @param max Maximum translational speed.
     */
    public static void normalize(SwerveModuleState[] states, final double max) {
        double top = 0, buff;
        for (final SwerveModuleState state : states)
            if ((buff = (state.speedMetersPerSecond / max)) > top) top = buff;
        if (top != 0)
            for (SwerveModuleState state : states) state.speedMetersPerSecond /= top;
    }

    // public static double coterminal(final double rotation) {
    //     return rotation % (2 * Math.PI);
    // }

    private static double coterminal(final double rotation) {
        double coterminal = rotation;
        final double full = Math.signum(rotation) * 2 * Math.PI;
        while (coterminal > Math.PI || coterminal < -Math.PI) coterminal -= full;
        return coterminal;
    }

    public static final class SwervePorts {
        public final int drive, turn, encoder;

        public SwervePorts(final int drive, final int turn, final int encoder) {
            this.drive = drive;
            this.turn = turn;
            this.encoder = encoder;
        }
    }
}
