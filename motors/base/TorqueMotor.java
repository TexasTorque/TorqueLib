package org.texastorque.torquelib.motors.base;

public abstract class TorqueMotor {
    protected int port;
    protected boolean invert;

    public abstract void set(double speed);

    public abstract void addFollower(int port);

    public final int getPort() {
        return port;
    }

    public final boolean isInverted() {
        return invert;
    }
}
