package org.texastorque.torquelib.control;

/**
 * Improvments to the algorithm used to drive a West Coast Drivetrain
 * durring teleop. Encapsulated in a class.
 * 
 * @author Justusß
 */
public final class TorqueWCDTeleopDriver {
    private final double power = 4;
    private double forward = 3, turn = .2, coef = 1, left = 0, right = 0;

    public TorqueWCDTeleopDriver(final double forward, final double turn) {
        this.forward = forward;
        this.turn = turn;
    }

    @Deprecated
    public final void setCoef(final double coef) {
        this.coef = coef;
    }

    public final void update(final double x, final double y) {
        left = -coef * (-turn * y - forward * Math.pow(x, power) * Math.signum(x));
        right = coef * (turn * y - forward * Math.pow(x, power) * Math.signum(x));
    }

    public final double getLeft() {
        return left;
    }

    public final double getRight() {
        return right;
    }

    public final void setForwardCoef(double forward) {
        this.forward = forward;
    }

    public final void setTurnCoef(double turn) {
        this.turn = turn;
    }

    public final void setLeft(double left) {
        this.left = left;
    }

    public final void setRight(double right) {
        this.right = right;
    }
}