package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

import edu.wpi.first.math.controller.PIDController;

/**
 * A class representation of a PID controller with 
 * control loop methods.
 * 
 * @apiNote Functional replacement for KPID
 * ({@link org.texastorque.torquelib.util.KPID})
 * 
 * TODO: Documentation
 * TODO: Implement PID calculation functions
 * TODO: Replace KPID in the rest of the codebase
 * 
 * @author Justus Languell
 */
public final class TorquePID {

    public final double calculate() {
        TorqueUtil.notImplemented();
        return 0.;
    }

    private final double proportional, integral, derivative, feedForward, minOutput, maxOutput, integralZone;
    private final boolean hasIntegralZone;

    public final PIDController createPIDController() {
        return new PIDController(proportional, integral, derivative);
    }

    public final PIDController createPIDController(final double period) {
        return new PIDController(proportional, integral, derivative, period);
    }

    private TorquePID(final Builder b) {
        proportional = b.proportional;
        integral = b.integral;
        derivative = b.derivative;
        feedForward = b.feedForward;
        minOutput = b.minOutput;
        maxOutput = b.maxOutput;
        integralZone = b.integralZone;
        hasIntegralZone = b.hasIntegralZone;
    }

    public static final Builder create() {
        return new Builder(1);
    }

    public static final Builder create(final double p) {
        return new Builder(p);
    }

    public static final class Builder {
        private double proportional, integral = 0, derivative = 0, feedForward = 0,
                       minOutput = -1, maxOutput = 1, integralZone = 0;
        private boolean hasIntegralZone = false;

        private Builder(final double proportional) { 
            this.proportional = proportional; 
        }

        public final Builder addIntegral(final double integral) {
            this.integral = integral;
            return this;
        }

        public final Builder addDerivative(final double derivative) {
            this.derivative = derivative;
            return this;
        }

        public final Builder addFeedForward(final double feedForward) {
            this.feedForward = feedForward;
            return this;
        }

        public final Builder addMinOutput(final double minOutput) {
            this.minOutput = minOutput;
            return this;
        }

        public final Builder addMaxOutput(final double maxOutput) {
            this.maxOutput = maxOutput;
            return this;
        }

        public final Builder addOutputRange(final double minOutput, final double maxOutput) {
            this.minOutput = minOutput;
            this.maxOutput = maxOutput;
            return this;
        }

        public final Builder addIntegralZone(final double integralZone) {
            this.hasIntegralZone = true;
            this.integralZone = integralZone;
            return this;
        }

        public final TorquePID build() { return new TorquePID(this); }
    }

    public final double getProportional() {
        return proportional;
    }

    public final double getIntegral() {
        return integral;
    }

    public final double getDerivative() {
        return derivative;
    }

    public final double getFeedForward() {
        return feedForward;
    }

    public final double getMinOutput() {
        return minOutput;
    }

    public final double getMaxOutput() {
        return maxOutput;
    }

    public final double getIntegralZone() {
        return integralZone;
    }

    public final boolean hasIntegralZone() {
        return hasIntegralZone;
    }

    @Override
    public final String toString() {
        return String.format("PID(PRO: %3.2f, INT: %3.2f, DER: %3.2f, F: %3.2f, MIN: %3.2f, MAX: %3.2f, IZO: %3.2f)",
            proportional, integral, derivative, feedForward, minOutput, maxOutput, integralZone);
        
    }
}
