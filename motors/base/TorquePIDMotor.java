package org.texastorque.torquelib.motors.base;

import org.texastorque.torquelib.util.KPID;

/**
 * Interface to include PID methods for a PID motor.
 *
 * @author Justus Languell
 */
public interface TorquePIDMotor {
    public final double CLICKS_PER_ROTATION = 0;

    public void configurePID(final KPID kPID);

    public void setPosition(final double setpoint);
    public void setPositionDegrees(final double setpoint);
    public void setPositionRotations(final double setpoint);

    public void setVelocity(final double setpoint);
    public void setVelocityRPS(final double setpoint);
    public void setVelocityRPM(final double setpoint);
}
