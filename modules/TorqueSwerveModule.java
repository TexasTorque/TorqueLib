package org.texastorque.torquelib.modules;

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;

import org.texastorque.torquelib.motors.TorqueSparkMax;
import org.texastorque.torquelib.motors.TorqueTalon;
import org.texastorque.torquelib.motors.TorqueSparkMax.SmartMotionProfile;
import org.texastorque.torquelib.motors.util.TorqueSparkMaxMotionProfile;
import org.texastorque.torquelib.util.KPID;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.Timer;

public class TorqueSwerveModule {
    private final TorqueSparkMax drive;
    private final TorqueTalon rotate;

    private final int id;

    private double lastSpeed = 0, lastTime = Timer.getFPGATimestamp();

    public final static KPID DEFAULT_DRIVE_PID = new KPID(.00048464, 0, 0, 0, -1, 1, .2);
    public final static SimpleMotorFeedforward DEFAULT_DRIVE_FEED_FORWARD = new SimpleMotorFeedforward(.27024, 2.4076, .5153);
    public final static KPID DEFAULT_ROTATE_PID = new KPID(.3, 0, 0, 0, -1, 1);
    public final TorqueSparkMaxMotionProfile defaultSmartMotionProfile;

    public final double driveGearing, wheelRadiusMeters;

    public TorqueSwerveModule(final int id, final int drivePort, final int rotatePort, 
            final double driveGearing, final double wheelRadiusMeters) {
        this.id = id;
        drive = new TorqueSparkMax(drivePort);
        drive.configurePID(DEFAULT_DRIVE_PID);

        defaultSmartMotionProfile = new TorqueSparkMaxMotionProfile(
                metersPerSecondToEncoderPerMinute(4), 
                metersPerSecondToEncoderPerMinute(.1),
                metersPerSecondToEncoderPerMinute(2), 
                metersPerSecondToEncoderPerMinute(.1));

        drive.configureSmartMotion(defaultSmartMotionProfile, 0);
        drive.setSupplyLimit(40);
        drive.burnFlash();

        rotate = new TorqueTalon(rotatePort);
        rotate.setSupplyLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, .03));
        rotate.configurePID(DEFAULT_ROTATE_PID);
        rotate.zeroEncoder();

        this.driveGearing = driveGearing;
        this.wheelRadiusMeters = wheelRadiusMeters;
    }

    public TorqueSwerveModule(final int id, final int drivePort, final int rotatePort, 
            final double driveGearing, final double wheelRadiusMeters,
            final KPID drivePID, final KPID rotatePID, 
            final TorqueSparkMaxMotionProfile smartMotionProfile) {
        this.id = id;
        this.driveGearing = driveGearing;
        this.wheelRadiusMeters = wheelRadiusMeters;

        drive = new TorqueSparkMax(drivePort);
        drive.configurePID(drivePID);
        drive.configureSmartMotion(smartMotionProfile, 0);
        drive.setSupplyLimit(40);
        drive.burnFlash();

        rotate = new TorqueTalon(rotatePort);
        rotate.setSupplyLimit(new SupplyCurrentLimitConfiguration(true, 5, 10, .03));
        rotate.configurePID(rotatePID);
        rotate.zeroEncoder();

   
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
         *
         * @param metersPerSecond Speed in meters per second
         * @return Speed in encoder rotations per minute
         */
        public double metersPerSecondToEncoderPerMinute(double metersPerSecond) {
            return metersPerSecond * (60. / 1.) * (1 / (2 * Math.PI * wheelRadiusMeters)
                            * (1. / driveGearing);
    }

    /**
     *
     * @param encodersPerMinute Speed in encoder rotations per minute
     * @return Speed in meters per second
     */
    public double encoderPerMinuteToMetersPerSecond(double encodersPerMinute) {
            return encodersPerMinute * (1. / 60.) * (2 * Math.PI * wheelRadiusMeters / 1.)
                            * (driveGearing / 1.);
    }



}

