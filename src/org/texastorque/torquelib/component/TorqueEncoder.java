package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.CounterBase;

public abstract class TorqueEncoder {

    protected CounterBase encoder;

    protected double previousTime;
    protected double previousRate;

    protected int currentPosition;
    protected int previousPosition;

    protected double instantRate;
    protected double secantRate;
    protected double acceleration;

    public abstract void calc();

    public void reset() {
        encoder.reset();
    }

    public int get() {
        return currentPosition;
    }

    public double getInstantRate() {
        return instantRate;
    }
    
    public double getSecantRate() {
        return secantRate;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
