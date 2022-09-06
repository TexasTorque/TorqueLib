/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

import edu.wpi.first.math.controller.PIDController;
import java.util.function.Function;

/**
 * A class representation of a PID controller that extends
 * the WPILib PIDController.
 *
 * @apiNote Functional replacement for KPID
 * ({@link org.texastorque.torquelib.util.KPID})
 *
 * @author Justus Languell
 */
public final class TorquePID extends PIDController {

    // * Variable fields

    private final double proportional, integral, derivative, feedForward, minOutput, maxOutput, integralZone, period;
    private final boolean hasIntegralZone;

    // * Construction and builder

    private TorquePID(final Builder b) {
        super(b.proportional, b.integral, b.derivative, b.period);
        if (b.hasContinuousRange) enableContinuousInput(b.minInput, b.maxInput);
        if (b.tolerance != -1) setTolerance(b.tolerance);
        proportional = b.proportional;
        integral = b.integral;
        derivative = b.derivative;
        feedForward = b.feedForward;
        minOutput = b.minOutput;
        maxOutput = b.maxOutput;
        integralZone = b.integralZone;
        hasIntegralZone = b.hasIntegralZone;
        period = b.period;
    }

    public static final Builder create() { return new Builder(1); }

    public static final Builder create(final double p) { return new Builder(p); }

    public static final class Builder {
        private double proportional, integral = 0, derivative = 0, feedForward = 0, minOutput = -1, maxOutput = 1,
                                     integralZone = 0, period = .02, minInput = 0, maxInput = 0, tolerance = -1;
        private boolean hasIntegralZone = false, hasContinuousRange = false;

        private Builder(final double proportional) { this.proportional = proportional; }

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

        public final Builder addMinContinuousInput(final double min) {
            this.hasContinuousRange = true;
            this.minInput = min;
            return this;
        }

        public final Builder addMaxContinuousInput(final double max) {
            this.hasContinuousRange = true;
            this.maxInput = max;
            return this;
        }

        public final Builder addContinuousInputRange(final double min, final double max) {
            this.hasContinuousRange = true;
            this.minInput = min;
            this.maxInput = max;
            return this;
        }

        public final Builder setPeriod(final double period) {
            this.period = period;
            return this;
        }

        public final Builder setTolerance(final double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public final TorquePID build() { return new TorquePID(this); }
    }

    // * Getters

    public final double getProportional() { return proportional; }

    public final double getIntegral() { return integral; }

    public final double getDerivative() { return derivative; }

    public final double getFeedForward() { return feedForward; }

    public final double getMinOutput() { return minOutput; }

    public final double getMaxOutput() { return maxOutput; }

    public final double getIntegralZone() { return integralZone; }

    public final boolean hasIntegralZone() { return hasIntegralZone; }

    @Override
    public final String toString() {
        return String.format("PID(PRO: %3.2f, INT: %3.2f, DER: %3.2f, F: %3.2f, MIN: %3.2f, MAX: %3.2f, IZO: %3.2f)",
                             proportional, integral, derivative, feedForward, minOutput, maxOutput, integralZone);
    }

    // * PIDController generator methods

    public final PIDController createPIDController() {
        return new PIDController(proportional, integral, derivative, period);
    }

    public final PIDController createPIDController(final Function<PIDController, PIDController> function) {
        return function.apply(createPIDController());
    }
}
