/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.modules;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.texastorque.torquelib.control.TorqueClick;
import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.modules.base.TorqueSwerveModule;
import org.texastorque.torquelib.motors.TorqueSparkMax;
import org.texastorque.torquelib.motors.TorqueTalon;
import org.texastorque.torquelib.util.KPID;

/**
 * A representation of the 2021 Texas Torque custom swervedrive module.
 *
 * The module utalizes a Rev Neo driven by a Rev Spark Max for the drive,
 * and a Vex 775 Pro driven by a CTRE Talon SRX for the rotation.
 *
 * The default constants and configurations are tuned to the 2022 robot.
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueSwerveModule2021 extends TorqueSwerveModule {
    private final TorqueSparkMax drive;
    private final TorqueTalon rotate;

    private double lastSpeed = 0, lastTime = Timer.getFPGATimestamp(), maxVelocity;
    private final double driveGearing, wheelRadiusMeters;

    private final SimpleMotorFeedforward driveFeedForward;
    private final int invCoef;

    private boolean logging = false;

     /**
     * Construct a new TorqueSwerveModule2021.
     *
     * @param id                The id of the swerve module.
     * @param drivePort         The port (can id) of the drive motor.
     * @param rotatePort        The port (can id) of the rotation motor.
     * @param driveGearing      The drive motor gearing.
     * @param wheelRadiusMeters The radius of the wheel in meters.
     * @param drivePID          The drive motor PID.
     * @param rotatePID         The rotation motor PID.
     * @param maxVelocity       The maximum velocity of the drive motor in meters per second.
     * @param maxAcceleration   The maximum acceleration of the drive motor in meters per second per second.
     * @param driveFeedForward  The drive motor feed forward for autonomous.
     */
    public TorqueSwerveModule2021(final int id, final int drivePort, final int rotatePort, final double driveGearing,
                                  final double wheelRadiusMeters, final TorquePID drivePID, final TorquePID rotatePID,
                                  final double maxVelocity, final double maxAcceleration,
                                  final SimpleMotorFeedforward driveFeedForward) {
        super(id);
        // invCoef = ((id & 1) << 1) - 1; // WTF? -> invCoef = id % 2 == 0 ? -1 : 1;
        invCoef = 1;

        drive = new TorqueSparkMax(drivePort);
        drive.configurePID(drivePID);
        // drive.configureSmartMotion(
        //         metersPerSecondToEncoderPerMinute(this.maxVelocity = maxVelocity),
        //         metersPerSecondToEncoderPerMinute(.1),
        //         metersPerSecondToEncoderPerMinute(maxAcceleration),
        //         metersPerSecondToEncoderPerMinute(.1),
        //         0);

        drive.setSupplyLimit(40);
        drive.burnFlash();

        rotate = new TorqueTalon(rotatePort);
        rotate.setSupplyLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, .03));
        rotate.configurePID(rotatePID);
        rotate.zeroEncoder();

        this.driveGearing = driveGearing;
        this.wheelRadiusMeters = wheelRadiusMeters;
        this.driveFeedForward = driveFeedForward;
        this.maxVelocity = maxVelocity;
    }

    /**
     * Construct a new TorqueSwerveModule2021.
     *
     * @param id                The id of the swerve module.
     * @param drivePort         The port (can id) of the drive motor.
     * @param rotatePort        The port (can id) of the rotation motor.
     * @param driveGearing      The drive motor gearing.
     * @param wheelRadiusMeters The radius of the wheel in meters.
     * @param drivePID          The drive motor PID.
     * @param rotatePID         The rotation motor PID.
     * @param maxVelocity       The maximum velocity of the drive motor in meters per second.
     * @param maxAcceleration   The maximum acceleration of the drive motor in meters per second per second.
     * @param driveFeedForward  The drive motor feed forward for autonomous.
     * 
     * @deprecated Uses KPID, use TorquePID instead.
     */
    @Deprecated 
    public TorqueSwerveModule2021(final int id, final int drivePort, final int rotatePort, final double driveGearing,
                                  final double wheelRadiusMeters, final KPID drivePID, final KPID rotatePID,
                                  final double maxVelocity, final double maxAcceleration,
                                  final SimpleMotorFeedforward driveFeedForward) {
        super(id);
        // invCoef = ((id & 1) << 1) - 1; // WTF? -> invCoef = id % 2 == 0 ? -1 : 1;
        invCoef = 1;

        drive = new TorqueSparkMax(drivePort);
        drive.configurePID(drivePID);
        // drive.configureSmartMotion(
        //         metersPerSecondToEncoderPerMinute(this.maxVelocity = maxVelocity),
        //         metersPerSecondToEncoderPerMinute(.1),
        //         metersPerSecondToEncoderPerMinute(maxAcceleration),
        //         metersPerSecondToEncoderPerMinute(.1),
        //         0);

        drive.setSupplyLimit(40);
        drive.burnFlash();

        rotate = new TorqueTalon(rotatePort);
        rotate.setSupplyLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, .03));
        rotate.configurePID(rotatePID);
        rotate.zeroEncoder();

        this.driveGearing = driveGearing;
        this.wheelRadiusMeters = wheelRadiusMeters;
        this.driveFeedForward = driveFeedForward;
        this.maxVelocity = maxVelocity;
    }

    /**
     * Set the state of the swerve module.
     *
     * @param state The state of the swerve module.
     */
    @Override
    public final void setDesiredState(SwerveModuleState state) {
        state = SwerveModuleState.optimize(state, getRotation());

        final double requestedEncoderUnits =
                (state.angle.getDegrees() * invCoef * rotate.CLICKS_PER_ROTATION * 2 / 360);
        final double adjustedEncoderUnits =
                Math.IEEEremainder(requestedEncoderUnits - rotate.getPosition(), rotate.CLICKS_PER_ROTATION) +
                rotate.getPosition();

        rotate.setPosition(adjustedEncoderUnits);

        putNumber("ReqDeg", state.angle.getDegrees());
        putNumber("ReqEnc", requestedEncoderUnits);
        putNumber("RealEnc", rotate.getPosition());
        putNumber("RealDeg", getRotationDegrees());

        if (DriverStation.isTeleop()) {
            final double setpoint = Math.min(-state.speedMetersPerSecond / maxVelocity, 1.); //* invCoef;
            putNumber("ReqDrive", -state.speedMetersPerSecond);
            putNumber("PwrDrive", setpoint);
            drive.setPercent(setpoint);
            return;
        }

        final double currentTime = Timer.getFPGATimestamp();

        drive.setFeedForwardSmartVelocity(
                -metersPerSecondToEncoderPerMinute(state.speedMetersPerSecond),
                -driveFeedForward.calculate(lastSpeed, state.speedMetersPerSecond, currentTime - lastTime),
                ArbFFUnits.kVoltage);

        lastSpeed = state.speedMetersPerSecond;
        lastTime = currentTime;
    }

    /**
     * Gets the current swerve module state.
     *
     * @return The SwerveModuleState that represents the current state of the module.
     */
    @Override
    public final SwerveModuleState getState() {
        return new SwerveModuleState(encoderPerMinuteToMetersPerSecond(drive.getVelocityRPM()), getRotation());
    }

    /**
     * Gets the rotation of the swerve module as a Rotation2d.
     *
     * @return The Rotation2d that represents the motor rotation.
     */
    @Override
    public final Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getRotationDegrees());
    }

    /**
     * Convert encoder units from the Talon to degrees on the swerve module.
     *
     * @return The value in degrees [-180, 180]
     *
     * @author Jack Pittenger
     * @author Justus Languell
     */
    private final double getRotationDegrees() {
        double val = rotate.getPosition();
        if (val % rotate.CLICKS_PER_ROTATION == 0) val += .0001;
        double ret = val % rotate.CLICKS_PER_ROTATION * 180 / (rotate.CLICKS_PER_ROTATION);
        if (Math.signum(val) == -1 && Math.floor(val / rotate.CLICKS_PER_ROTATION) % 2 == -0)
            ret += 180;
        else if (Math.signum(val) == 1 && Math.floor(val / rotate.CLICKS_PER_ROTATION) % 2 == 1)
            ret -= 180;
        return ret * invCoef;
    }

    /**
     * Converts wheel speed in meters per second to rotations per minute.
     *
     * @param metersPerSecond Speed wheel in meters per second.
     *
     * @return Speed in rotations per minute.
     */
    public final double metersPerSecondToEncoderPerMinute(final double metersPerSecond) {
        return metersPerSecond * (60. / 1.) * (1 / (2 * Math.PI * wheelRadiusMeters) * (1. / driveGearing));
    }

    /**
     * Conveerts motor speed in rotations per minute to meters per second.
     *
     * @param encodersPerMinute Speed in rotations per minute.
     *
     * @return Wheel speed in meters per second.
     */
    public final double encoderPerMinuteToMetersPerSecond(final double encodersPerMinute) {
        return encodersPerMinute * (1. / 60.) * (2 * Math.PI * wheelRadiusMeters / 1.) * (driveGearing / 1.);
    }

    /**
     * Equalizes drive speeds to never exceed full power on the Neo.
     *
     * @param states The swerve module states, this is mutated!
     * @param max Maximum translational speed.
     */
    public static void equalizedDriveRatio(SwerveModuleState[] states, final double max) {
        double top = 0, buff;
        for (final SwerveModuleState state : states)
            if ((buff = (state.speedMetersPerSecond / max)) > top) top = buff;
        if (top != 0)
            for (SwerveModuleState state : states) state.speedMetersPerSecond /= top;
    }

    /**
     * Set the logging status of the module.
     *
     * @param logging To log or not to log.
     */
    public final void setLogging(final boolean logging) { this.logging = logging; }

    /**
     * Put number if this module is logging.
     *
     * @param key The key to smart dashboard.
     * @param value The value to log.
     */
    private final void putNumber(final String key, final double value) {
        if (logging) SmartDashboard.putNumber(String.format("(%d) %s", id, key), value);
    }

    public final double getDisplacement() {
        return drive.getPosition();
    }

    public final void spin(final double speed) {
        drive.setPercent(0);
        rotate.setPercent(speed);
    }

}
