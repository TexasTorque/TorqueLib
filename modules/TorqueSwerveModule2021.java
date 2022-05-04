package org.texastorque.torquelib.modules;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import org.texastorque.torquelib.modules.base.TorqueSwerveModule;
import org.texastorque.torquelib.motors.TorqueSparkMax;
import org.texastorque.torquelib.motors.TorqueTalon;
import org.texastorque.torquelib.util.KPID;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    public TorqueSwerveModule2021(final int id, final int drivePort, final int rotatePort, 
            final double driveGearing, final double wheelRadiusMeters,
            final KPID drivePID, final KPID rotatePID, 
            final double maxVelocity, final double maxAcceleration,
            final SimpleMotorFeedforward driveFeedForward) {
        super(id);

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

    }

    /**
     * Set the state of the swerve module.
     * 
     * @param state The state of the swerve module.
     */
    @Override
    public void setDesiredState(SwerveModuleState state) {
        state = SwerveModuleState.optimize(state, getRotation());

        rotate.setPosition(Math.IEEEremainder((state.angle.getDegrees() * rotate.CLICKS_PER_ROTATION / 360.) 
                - rotate.getPosition(), rotate.CLICKS_PER_ROTATION / 2.) + rotate.getPosition());

        if (id == 0) {

            SmartDashboard.putNumber("Req", state.angle.getDegrees());
            SmartDashboard.putNumber("Req", rotate.getPosition());
        }



        if (DriverStation.isTeleop()) {
            // drive.setPercent(-state.speedMetersPerSecond / maxVelocity);
            return;
        }

        final double currentTime = Timer.getFPGATimestamp();

        drive.setFeedForwardSmartVelocity(-metersPerSecondToEncoderPerMinute(state.speedMetersPerSecond), 
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
    public SwerveModuleState getState() {
        return new SwerveModuleState(encoderPerMinuteToMetersPerSecond(drive.getVelocityRPM()), getRotation());
    }

    /**
     * Gets the rotation of the swerve module as a Rotation2d.
     * 
     * @return The Rotation2d that represents the motor rotation.
     */
    @Override
    public Rotation2d getRotation() {
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
    private double getRotationDegrees() {
        double val = rotate.getPosition();
        if (val % rotate.CLICKS_PER_ROTATION == 0) val += .0001;
        double ret = val % rotate.CLICKS_PER_ROTATION * 180 / (rotate.CLICKS_PER_ROTATION);
        if (Math.signum(val) == -1 && Math.floor(val / rotate.CLICKS_PER_ROTATION) % 2 == -0)
                ret += 180;
        else if (Math.signum(val) == 1 && Math.floor(val / rotate.CLICKS_PER_ROTATION) % 2 == 1)
                ret -= 180;
        return ret;
    }

    /**
     * Converts wheel speed in meters per second to rotations per minute.
     *
     * @param metersPerSecond Speed wheel in meters per second.
     * 
     * @return Speed in rotations per minute.
     */
    public double metersPerSecondToEncoderPerMinute(double metersPerSecond) {
        return metersPerSecond * (60. / 1.) * (1 / (2 * Math.PI * wheelRadiusMeters)
                        * (1. / driveGearing));
    }

    /**
     * Conveerts motor speed in rotations per minute to meters per second.
     * 
     * @param encodersPerMinute Speed in rotations per minute.
     * 
     * @return Wheel speed in meters per second.
     */
    public double encoderPerMinuteToMetersPerSecond(double encodersPerMinute) {
            return encodersPerMinute * (1. / 60.) * (2 * Math.PI * wheelRadiusMeters / 1.)
                            * (driveGearing / 1.);
    }

}

