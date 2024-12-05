
/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve;

import org.texastorque.torquelib.motors.TorqueNEO;
import org.texastorque.torquelib.swerve.base.TorqueSwerveModule;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    private static double coterminal(final double rotation) {
        double coterminal = rotation;
        final double full = Math.signum(rotation) * 2 * Math.PI;
        while (coterminal > Math.PI || coterminal < -Math.PI) coterminal -= full;
        return coterminal;
    }
    private final SwerveConfig config;

    // The NEO motors for turn and drive.
    private final TorqueNEO drive, turn;

    // The CANCoder for wheel angle measurement.
    private final CANcoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFF;

    // The name of the module that we can use for SmartDashboard outputs
    public final String name;

    public boolean useCancoder = true;

    public TorqueSwerveModule2022(final String name, final SwervePorts ports) {
        super(name);
        this.name = name.replaceAll(" ", "_").toLowerCase();

        config = SwerveConfig.defaultConfig;

        // Configure the drive motor.
        drive = new TorqueNEO(ports.drive);
        drive.setCurrentLimit(config.driveMaxCurrent);
        drive.setVoltageCompensation(config.voltageCompensation);
        drive.setBreakMode(true);
        drive.invertMotor(false);
        drive.setConversionFactors(config.drivePoseFactor, config.driveVelocityFactor);
        drive.burnFlash();

        // Configure the turn motor.
        turn = new TorqueNEO(ports.turn);
        turn.setConversionFactors(config.turnGearRatio * 2 * Math.PI, 1);
        turn.setCurrentLimit(config.turnMaxCurrent);
        turn.setVoltageCompensation(config.voltageCompensation);
        turn.setBreakMode(true);
        turn.burnFlash();

        cancoder = new CANcoder(ports.encoder);

        // Configure the controllers
        drivePID = new PIDController(config.drivePGain, config.driveIGain, config.driveDGain);
        turnPID = new PIDController(config.turnPGain, config.turnIGain, config.turnDGain);
        turnPID.enableContinuousInput(-Math.PI, Math.PI);
        driveFF = new SimpleMotorFeedforward(config.driveStaticGain, config.driveFeedForward);
    }

    @Override
    public void setDesiredState(final SwerveModuleState state) {
        setDesiredState(state, DriverStation.isAutonomous());
    }

    public void setDesiredState(final SwerveModuleState state, final boolean useSmartDrive) {
        final SwerveModuleState optimized = SwerveModuleState.optimize(state, getRotation());

        // Calculate drive output
        if (useSmartDrive) {
            final double drivePIDOutput = drivePID.calculate(drive.getVelocity(), optimized.speedMetersPerSecond);
            final double driveFFOutput = driveFF.calculate(optimized.speedMetersPerSecond);
            log("Drive PID Output", drivePIDOutput + driveFFOutput);
            drive.setPercent(drivePIDOutput + driveFFOutput);
        } else {
            drive.setPercent(optimized.speedMetersPerSecond / 4.6);
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
        return Rotation2d.fromRadians(getTurnEncoder());
    }

    public void stop() {
        drive.setPercent(0.0);
        turn.setPercent(0.0);
    }

    public void zero() { turn.setPercent(log("zero pid", turnPID.calculate(getTurnEncoder(), 0))); }

    private double getTurnEncoder() {
		return (cancoder.getAbsolutePosition().getValue() * 2 * Math.PI) % (2 * Math.PI);
	}

    private double log(final String item, final double value) {
        final String key = name + "." + item.replaceAll(" ", "_").toLowerCase();
        SmartDashboard.putNumber(String.format("%s::%s", name, key), value);
        return value;
    }
}
