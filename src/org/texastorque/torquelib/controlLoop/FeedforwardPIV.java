package org.texastorque.torquelib.controlLoop;

import edu.wpi.first.wpilibj.util.BoundaryException;

/**
 * Class implements a PIV Control Loop.
 *
 * Does all computation synchronously (i.e. the calculate() function must be
 * called by the user from his own thread)
 *
 * Note: edited from original version from 341... onTarget() will only work if
 * using this class with TMP.
 */
public class FeedforwardPIV {

    private double m_P;			// factor for "proportional" control
    private double m_I;			// factor for "integral" control
    private double m_V;			// factor for "derivative of velocity" control
    private double m_ffV; // feedforward velocity
    private double m_ffA; // feedforward acceleration
    private double m_maximumOutput = 1.0;	// |maximum output|
    private double m_minimumOutput = -1.0;	// |minimum output|
    private double m_maximumInput = 0.0;		// maximum input - limit setpoint to this
    private double m_minimumInput = 0.0;		// minimum input - limit setpoint to this
    private double m_prevError = 0.0;	// the prior sensor input (used to compute velocity)
    private double m_integralError = 0.0; //the sum of the errors for use in the integral calc
    protected double m_setpoint = 0.0;
    private double m_error = 0.0;
    private double m_result = 0.0;
    private double m_last_input = Double.NaN;

    public FeedforwardPIV() {
        m_P = m_I = m_V = m_ffV = m_ffA = 0.0;
    }

    /**
     * Allocate a PID object with the given constants for P, I, V
     *
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kv the velocity differential coefficient
     * @param KffV the feedforward velocity gain
     * @param KffA the feedforward acceleration gain
     */
    public FeedforwardPIV(double Kp, double Ki, double Kv, double KffV, double KffA) {
        setParams(Kp, Ki, Kv, KffV, KffA);
    }

    public synchronized double calculate(double desiredPosition, double desiredSpeed, double desiredAccel, double currentPosition, double currentVelocity, double dt) {
        m_last_input = currentPosition;
        m_error = desiredPosition - currentPosition;

        m_integralError += m_error - currentPosition;

        m_result = (m_P * m_error + m_I * m_integralError + m_V * ((m_error - m_prevError) / dt - desiredSpeed)) + m_ffV * desiredSpeed + m_ffA * desiredAccel;
        m_prevError = m_error;

        if (m_result > m_maximumOutput) {
            m_result = m_maximumOutput;
        } else if (m_result < m_minimumOutput) {
            m_result = m_minimumOutput;
        }
        return m_result;
    }

    /**
     * Read the input, calculate the output accordingly, and write to the
     * output. This should be called at a constant rate by the user (ex. in a
     * timed thread)
     */
    public synchronized double calculate(TrapezoidalProfile trajectory, double currentPosition, double currentVelocity, double dt) {
        return calculate(trajectory.getPosition(), trajectory.getVelocity(), trajectory.getAcceleration(), currentPosition, currentVelocity, dt);
    }

    /**
     * Return the current PID result This is always centered on zero and
     * constrained the the max and min outs
     *
     * @return the latest calculated output
     */
    public synchronized double get() {
        return m_result;
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the output
     */
    public synchronized void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumInput = minimumInput;
        m_maximumInput = maximumInput;
        setSetpoint(m_setpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum value to write to the output
     * @param maximumOutput the maximum value to write to the output
     */
    public synchronized void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumOutput = minimumOutput;
        m_maximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PID controller
     *
     * @param setpoint the desired setpoint
     */
    public synchronized void setSetpoint(double setpoint) {
        if (m_maximumInput > m_minimumInput) {
            if (setpoint > m_maximumInput) {
                m_setpoint = m_maximumInput;
            } else if (setpoint < m_minimumInput) {
                m_setpoint = m_minimumInput;
            } else {
                m_setpoint = setpoint;
            }
        } else {
            m_setpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PID controller
     *
     * @return the current setpoint
     */
    public synchronized double getSetpoint() {
        return m_setpoint;
    }

    /**
     * Returns the current difference of the input from the setpoint
     *
     * @return the current error
     */
    public synchronized double getError() {
        return m_error;
    }

    /**
     * Return true if the error is within the tolerance
     *
     * @return true if the error is less than the tolerance
     */
    public synchronized boolean onTarget(double tolerance) {
        if (m_last_input != Double.NaN && Math.abs(m_last_input/* - m_setpoint */) < tolerance) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reset all internal terms.
     */
    public synchronized void reset() {
        m_last_input = Double.NaN;
        m_prevError = 0;
        m_integralError = 0;
        m_result = 0;
        m_setpoint = 0;
    }

    public synchronized String getState() {
        String lState = "";

        lState += "Kp: " + m_P + "\n";
        lState += "Ki: " + m_I + "\n";
        lState += "Kv: " + m_V + "\n";
        lState += "KffV: " + m_ffV + "\n";
        lState += "KffA: " + m_ffA + "\n";

        return lState;
    }

    public synchronized final void setParams(double Kp, double Ki, double Kv, double KffV, double KffA) {
        m_P = Kp;
        m_I = Ki;
        m_V = Kv;
        m_ffV = KffV;
        m_ffA = KffA;
    }
}
