package org.texastorque.torquelib.controlLoop;

public abstract class ControlLoop {

    protected double setPoint;
    protected double currentValue;

    protected double doneRange;
    protected int minDoneCycles;
    protected int doneCyclesCount;

    public ControlLoop() {
        setPoint = 0;
        doneRange = 0;
    }

    public void setSetpoint(double set) {
        setPoint = set;
    }

    public void setDoneRange(double range) {
        doneRange = range;
    }

    public void setDoneCycles(int cycles) {
        minDoneCycles = cycles;
    }

    public boolean isDone() {
        double currError = Math.abs(setPoint - currentValue);

        if (currError <= this.doneRange) {
            doneCyclesCount++;
        } else {
            doneCyclesCount = 0;
        }

        return doneCyclesCount > minDoneCycles;
    }

    public abstract double calculate(double current);
}
