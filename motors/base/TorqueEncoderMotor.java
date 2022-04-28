package org.texastorque.torquelib.motors.base;

/**
 * Interface to include encoder feedback methods for motors.
 * 
 * @author Justus
 */
public interface TorqueEncoderMotor {

    public double getPosition();
    public double getPositionDegrees();
    public double getPositionRotations();

    public double getVelocity();
    public double getVelocityRPS();
    public double getVelocityRPM();
}