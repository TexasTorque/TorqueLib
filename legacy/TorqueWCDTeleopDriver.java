/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.legacy;

/**
 * Improvments to the algorithm used to drive a West Coast Drivetrain
 * durring teleop. Encapsulated in a class.
 *
 * @author Justus
 */
public class TorqueWCDTeleopDriver {
    private double forward = 3;
    private double turn = .2;

    private final double power = 4;
    private double coef = 1;

    double left = 0;
    double right = 0;

    public TorqueWCDTeleopDriver(double forward, double turn) {
        this.forward = forward;
        this.turn = turn;
    }

    /**
     * This will be removed after fully debugged
     *
     * @param coef
     * @deprecated
     */
    @Deprecated
    public void setCoef(double coef) {
        this.coef = coef;
    }

    public void update(double x, double y) {
        left = -coef * (-turn * y - forward * Math.pow(x, power) * Math.signum(x));
        right = coef * (turn * y - forward * Math.pow(x, power) * Math.signum(x));
    }

    public double getLeft() { return left; }

    public double getRight() { return right; }

    public void setForwardCoef(double forward) { this.forward = forward; }

    public void setTurnCoef(double turn) { this.turn = turn; }

    public void setLeft(double left) { this.left = left; }

    public void setRight(double right) { this.right = right; }
}
