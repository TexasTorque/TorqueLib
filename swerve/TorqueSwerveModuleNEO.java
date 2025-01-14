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
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

/**
 * Super cool flipped swerve module built in 2023 by Abishek.
 * Updated to work with WCP Swerve X modules for 2024.
 * 
 * The module uses a Rev NEO for drive and rotation control. It uses a CTRE
 * Cancoder
 * positioned 1:1 with the wheel rotation.
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
public final class TorqueSwerveModuleNEO extends TorqueSwerveModule {

   
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


    // The NEO motors for turn and drive.
    private final TorqueNEO drive, turn;

    // The CANCoder for wheel angle measurement.
    private final CANcoder cancoder;

    // Velocity controllers.
    private final PIDController drivePID, turnPID;

    private final SimpleMotorFeedforward driveFeedForward;

    public boolean useCancoder = true;

    public static final double maxVelocity = 4.2;

    public TorqueSwerveModuleNEO(final String name, final SwervePorts ports) {
        super(name);

        final int driveMaxCurrentSupply = 35, // amps
                turnMaxCurrent = 25; // amps
        final double voltageCompensation = 12.6, // volts
                // The following will most likely need to be overriden
                // depending on the weight of each robot
                driveStaticGain = 0.015, driveFFGain = 0.2485, drivePGain = 0.1, driveIGain = 0.0,
                driveDGain = 0.0,

                driveGearRatio = 6.75, // Translation motor to wheel
                wheelDiameter = 4.0 * 0.0254, // m
                driveVelocityFactor = (1.0 / driveGearRatio / 60.0) * (wheelDiameter * Math.PI), // m/s
                drivePoseFactor = (1.0 / driveGearRatio) * (wheelDiameter * Math.PI), // m
                turnPGain = 0.5, turnIGain = 0.0, turnDGain = 0.0,
                turnGearRatio = 468.0 / 35.0; // Rotation motor to wheel

        // Configure the drive motor.
        drive = new TorqueNEO(ports.drive)
                .currentLimit(driveMaxCurrentSupply)
                .voltageCompensation(voltageCompensation)
                .idleMode(IdleMode.kBrake)
                .inverted(false)
                .conversionFactors(drivePoseFactor, driveVelocityFactor)
                .apply();

        // Configure the turn motor.
        turn = new TorqueNEO(ports.turn)
                .conversionFactors(turnGearRatio * 2 * Math.PI, 1)
                .currentLimit(turnMaxCurrent)
                .voltageCompensation(voltageCompensation)
                .idleMode(IdleMode.kBrake)
                .apply();

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
        state.optimize(getRotation());

        // Calculate drive output
        if (useSmartDrive) {
            final double drivePIDOutput = drivePID.calculate(drive.getVelocity(), state.speedMetersPerSecond);
            final double driveFFOutput = driveFeedForward.calculate(state.speedMetersPerSecond);
            
            drive.setPercent(drivePIDOutput + driveFFOutput);
        } else {
            final double driveOutput = state.speedMetersPerSecond / maxVelocity;
            drive.setPercent(driveOutput);
        }

        Debug.log(name + " Real Velocity", Math.abs(drive.getVelocity()));
        Debug.log(name + " Req Velocity", state.speedMetersPerSecond);
        Debug.log("Max Velocity", maxVelocity);

        // Calculate turn output
        double turnPIDOutput = -turnPID.calculate(getTurnEncoder(), state.angle.getRadians());
        turn.setPercent(turnPIDOutput);

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
        return new SwerveModuleState(drive.getVelocity(), getRotation());
    }

    @Override
    public SwerveModulePosition getPosition() {
        if (!RobotBase.isReal()) {
            return aggregatePosition;
        }
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
        double absAngle = Math.toRadians(cancoder.getAbsolutePosition().getValue().magnitude() * 360);
        absAngle %= 2.0 * Math.PI;
        if (absAngle < 0.0) {
            absAngle += 2.0 * Math.PI;
        }
        return absAngle;
    }

}
