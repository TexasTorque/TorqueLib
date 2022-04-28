package org.texastorque.torquelib.motors.base;

public abstract class TorqueMotor {
    protected int port;

    public TorqueMotor(int port) {
        this.port = port;
    }

    public abstract void setPercent(double percent);

    public abstract void addFollower(int port);
}
