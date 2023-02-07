/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * A logistic regression model that can be used to calculate speed from error.
 *
 * @author Jack Pittenger
 */
public final class TorqueLogisticRegression {
    private final double MAX_SPEED;
    private final double MAX_DISTANCE;
    private final double GROWTH;

    public TorqueLogisticRegression(final double maxSpeed, final double maxDistance, final double growth) {
        this.MAX_DISTANCE = maxDistance;
        this.MAX_SPEED = maxSpeed;
        this.GROWTH = growth;
    }

    public double calculate(final double current, final double requested) {
        final double error = current - requested;
        return (z(error) - z(0)) * Math.signum(error);
    }

    private double z(final double error) {
        return MAX_SPEED / (Math.pow(1 + Math.E, -GROWTH * (error - MAX_DISTANCE / 2.0)));
    }
}
