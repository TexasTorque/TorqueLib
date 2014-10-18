package org.texastorque.torquelib.controlLoop;

public class TorquePID {

    private double kFF;
    private double kP;
    private double kI;
    private double kD;
    private double epsilon;
    private double doneRange;
    private double setpoint;
    private double previousValue;
    private double errorSum;
    private boolean firstCycle;
    private double maxOutput;
    private int minCycleCount;
    private int cycleCount;

    public TorquePID() {
        this(0.0, 0.0, 0.0);
    }

    public TorquePID(double p, double i, double d) {
        kP = p;
        kI = i;
        kD = d;
        epsilon = 0.0;
        kFF = 0.0;
        doneRange = 0.0;
        setpoint = 0.0;
        previousValue = 0.0;
        errorSum = 0.0;
        firstCycle = true;
        maxOutput = 1.0;
        minCycleCount = 10;
        cycleCount = 0;
    }

    public void setPIDGains(double p, double i, double d) {
        kP = p;
        kI = i;
        kD = d;
    }

    public void setFeedForward(double ff) {
        kFF = ff;
    }

    public void setEpsilon(double e) {
        epsilon = e;
    }

    public void setDoneRange(double range) {
        doneRange = range;
    }

    public void setSetpoint(double sp) {
        setpoint = sp;
    }

    public void setMaxOutput(double max) {
        if (max < 0.0) {
            maxOutput = 0.0;
        } else if (max > 1.0) {
            maxOutput = 1.0;
        } else {
            maxOutput = max;
        }
    }

    public void setMinDoneCycles(int num) {
        minCycleCount = num;
    }

    public void reset() {
        errorSum = 0.0;
        firstCycle = true;
    }

    public double getSetpoint() {
        return setpoint;
    }

    public double getPreviousValue() {
        return previousValue;
    }

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
        ffVal = setpoint * kFF;

        //----- P Calculation -----
        double error = setpoint - currentValue;

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

    public boolean isDone() {
        double currError = Math.abs(setpoint - previousValue);

        if (currError <= this.doneRange) {
            cycleCount++;
        } else {
            cycleCount = 0;
        }

        return cycleCount > minCycleCount;
    }
}
