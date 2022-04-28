package org.texastorque.torquelib.motors.base;

public abstract class TorqueMotor {
    protected final int port;

    public TorqueMotor(final int port) {
        this.port = port;
    }

    public abstract void setPercent(final double percent);

    public abstract void addFollower(final int port);
}
