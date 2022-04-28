package org.texastorque.torquelib.motors.base;

import org.texastorque.torquelib.util.KPID;

/**
 * Interface to include PID methods for a PID motor.
 */
public interface TorquePIDMotor {
    public void configurePID(final KPID kPID);

    public void setPosition(final double setpoint);
    public void setPositionDegrees(final double setpoint);
    public void setPositionRotations(final double setpoint);

    public void setVelocity(final double setpoint);
    public void setVelocityRPS(final double setpoint);
    public void setVelocityRPM(final double setpoint);
}
