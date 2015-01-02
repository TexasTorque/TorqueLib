package org.texastorque.torquelib.controlLoop;

import java.util.function.DoubleUnaryOperator;

/**
 * A PID implementation.
 *
 * @author TexasTorque
 */
public class TorquePID extends ControlLoop {

    private double kFF;
    private DoubleUnaryOperator feedForwardFunction;
    private double kP;
    private double kI;
    private double kD;
    private double epsilon;
    private double previousValue;
    private double errorSum;
    private boolean firstCycle;
    private double maxOutput;

    /**
     * Create a new PID with all constants 0.0.
     */
    public TorquePID() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * Create a new PID.
     *
     * @param p The proportionality constant.
     * @param i The integral constant.
     * @param d The derivative constant.
     */
    public TorquePID(double p, double i, double d) {
        super();
        kP = p;
        kI = i;
        kD = d;
        epsilon = 0.0;
        kFF = 0.0;
        feedForwardFunction = (x) -> x;
        doneRange = 0.0;
        previousValue = 0.0;
        errorSum = 0.0;
        firstCycle = true;
        maxOutput = 1.0;
        minDoneCycles = 10;
    }

    /**
     * Change the PID constants.
     *
     * @param p The proportionality constant.
     * @param i The integral constant.
     * @param d The derivative constant.
     */
    public void setPIDGains(double p, double i, double d) {
        kP = p;
        kI = i;
        kD = d;
    }

    /**
     * Set the feedforward constant.
     *
     * @param operator The object used to calculate the feedforward variable.
     * @param ff The feedforward constant.
     * @see java.util.function.DoubleUnaryOperator
     */
    public void setFeedForward(DoubleUnaryOperator operator, double ff) {
        feedForwardFunction = operator;
        kFF = ff;
    }

    /**
     * Set the feedforward constant using the function y=x as the
     * DoubleUnaryOperator.
     *
     * @param ff The feedforward constant.
     */
    public void setFeedForward(double ff) {
        feedForwardFunction = (x) -> x;
        kFF = ff;
    }

    /**
     * Set the epsilon value.
     * 
     * @param e The new epsilon value.
     */
    public void setEpsilon(double e) {
        epsilon = e;
    }

    /**
     * Set the limit of the output.
     *
     * @param max The maximum value that the value can be.
     */
    public void setMaxOutput(double max) {
        if (max < 0.0) {
            maxOutput = 0.0;
        } else if (max > 1.0) {
            maxOutput = 1.0;
        } else {
            maxOutput = max;
        }
    }

    /**
     * Reset the PID controller.
     */
    public void reset() {
        errorSum = 0.0;
        firstCycle = true;
    }

    public double getPreviousValue() {
        return previousValue;
    }

    /**
     * Calculate output based off of the current sensor value.
     * 
     * @param currentValue the current sensor feedback.
     * @return Motor ouput to the system.
     */
    @Override
    public double calculate(double currentValue) {
        double ffVal = 0.0;
        double pVal = 0.0;
        double iVal = 0.0;
        double dVal = 0.0;

        if (firstCycle) {
            previousValue = currentValue;
            firstCycle = false;
        }

        //----- FF Calculation -----
        ffVal = feedForwardFunction.applyAsDouble(setPoint) * kFF;

        //----- P Calculation -----
        double error = setPoint - currentValue;

        pVal = kP * error;

        //----- I Calculation -----
        if (error > epsilon) {
            if (errorSum < 0.0) {
                errorSum = 0.0;
            }
            errorSum += Math.min(error, 1.0);
        } else {
            errorSum = 0.0;
        }

        iVal = kI * errorSum;

        //----- D Calculation -----
        double deriv = currentValue - previousValue;

        dVal = kD * deriv;

        //---- Combine Calculations -----
        double output = ffVal + pVal + iVal - dVal;

        //---- Limit Output -----
        if (output > maxOutput) {
            output = maxOutput;
        } else if (output < -maxOutput) {
            output = -maxOutput;
        }

        //----- Save Value -----
        previousValue = currentValue;

        return output;
    }

}
