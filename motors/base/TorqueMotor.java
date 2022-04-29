package org.texastorque.torquelib.motors.base;

/**
 * A base class for Texas Torque motor controller wrappers.
 * 
 * @author Justus Languell
 */
public abstract class TorqueMotor {
    protected final int port;

    protected TorqueMotor(final int port) {
        this.port = port;
    }

    public abstract void setPercent(final double percent);

    public abstract void addFollower(final int port);

    public abstract void invert(final boolean invert);
}
