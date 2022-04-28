package org.texastorque.torquelib.motors.base;

import org.texastorque.torquelib.util.KPID;

/**
 * Interface to include PID methods for a PID motor.
 */
public interface TorquePIDMotor {
    public void configurePID(KPID kPID);

    public double setPosition();
    public double setPositionDegrees();
    public double setPositionRotations();

    public double setVelocity();
    public double setVelocityRPS();
    public double setVelocityRPM();
}
