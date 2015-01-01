package org.texastorque.torquelib.controlLoop;

/**
 * Superclass for all controllers.
 *
 * @author TexasTorque
 */
public abstract class ControlLoop {

    protected double setPoint;
    protected double currentValue;

    protected double doneRange;
    protected int minDoneCycles;
    protected int doneCyclesCount;

    /**
     * Create a new control loop.
     */
    public ControlLoop() {
        setPoint = 0;
        doneRange = 0;
    }

    /**
     * Set the setpoint.
     *
     * @param set The new setpoint.
     */
    public void setSetpoint(double set) {
        setPoint = set;
    }

    /**
     * Set the range between which the controller treats as the setpoint.
     *
     * @param range The new range.
     */
    public void setDoneRange(double range) {
        doneRange = range;
    }

    /**
     * Minimum number of times the controller hits the setpoint range.
     *
     * @param cycles Number of times.
     */
    public void setDoneCycles(int cycles) {
        minDoneCycles = cycles;
    }

    /**
     * Get whether or not the controller has reached the setpoint and satisfied
     * the range requirement.
     *
     * @return True means that it has completed enough done cycles.
     */
    public boolean isDone() {
        double currError = Math.abs(setPoint - currentValue);

        if (currError <= this.doneRange) {
            doneCyclesCount++;
        } else {
            doneCyclesCount = 0;
        }

        return doneCyclesCount > minDoneCycles;
    }

    /**
     * Calculate the current output for the system that is being controlled.
     *
     * @param current The current parameter.
     * @return What the output should be.
     */
    public abstract double calculate(double current);
}
