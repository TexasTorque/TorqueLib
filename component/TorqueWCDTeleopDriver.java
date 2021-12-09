package org.texastorque.torquelib.component;

/**
 * Improvments to the algorithm used to drive a West Coast Drivetrain
 * durring teleop. Encapsulated in a class.
 * 
 * @author Justus
 */
public class TorqueWCDTeleopDriver {
    private double forward = 1;
    private double turn = 1;

    private final double power = 4;
    private double coef = .2;

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

    public void update(double y, double x) {
        left = -coef * (-forward * y - turn * Math.pow(x, power) * Math.signum(x));
        right = coef * ( forward * y - turn * Math.pow(x, power) * Math.signum(x));
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public void setForwardCoef(double forward) {
        this.forward = forward;
    }

    public void setTurnCoef(double turn) {
        this.turn = turn;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
