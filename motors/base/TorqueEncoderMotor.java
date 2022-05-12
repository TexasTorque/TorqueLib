package org.texastorque.torquelib.motors.base;

/**
 * Interface to include encoder feedback methods for motors.
 *
 * @author Justus Languell
 */
public interface TorqueEncoderMotor {

    public double getPosition();
    public double getPositionDegrees();
    public double getPositionRotations();

    public double getVelocity();
    public double getVelocityRPS();
    public double getVelocityRPM();

    public double getAcceleration();
    public double getAccelerationRPS();
    public double getAccelerationRPM();

    /**
     * Finds velocity of a drive wheel in RPMs.
     *
     * @param motor     Torque motor that implements TorqueEncoderMotor.
     * @param gearRatio The gear ratio of the wheel.
     *
     * @return RPM of the wheel.
     *
     * @author Jack Pittenger
     * @author Justus Languell
     */
    public static double getWheelRPM(TorqueEncoderMotor motor, double gearRatio) {
        return motor.getVelocityRPM() / gearRatio;
    }

    /**
     * Finds Velocity of a Drive Wheel in meters per second
     *
     * @param motor     Torque motor that implements TorqueEncoderMotor.
     * @param gearRatio The gear ratio of the wheel.
     * @param wheelRadiusMeters Radius of the wheel in meters.
     *
     * @return Velocity of the wheel in m/s.
     *
     * @author Jack Pittenger
     * @author Justus Languell
     */
    public static double getWheelVelocity(TorqueEncoderMotor motor, double gearRatio, double wheelRadiusMeters) {
        return getWheelRPM(motor, gearRatio) * 2 * Math.PI * wheelRadiusMeters / 60;
    }
}