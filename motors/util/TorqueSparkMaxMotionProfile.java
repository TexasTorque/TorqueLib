package org.texastorque.torquelib.motors.util;

/**
 * An object to represent a smart motion profile.
 * 
 * @author Justus Languell
 */
@Deprecated
public final class TorqueSparkMaxMotionProfile {
    private final double maxVelocity, minVelocity, maxAcceleration, allowedError;

    public TorqueSparkMaxMotionProfile(final double maxVelocity, 
                                final double minVelocity, 
                                final double maxAcceleration,
                                final double allowedError) {
        this.maxVelocity = maxVelocity;
        this.minVelocity = minVelocity;
        this.maxAcceleration = maxAcceleration;
        this.allowedError = allowedError;
    } 

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public double getMinVelocity() {
        return minVelocity;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public double getAllowedError() {
        return allowedError;
    }
}

