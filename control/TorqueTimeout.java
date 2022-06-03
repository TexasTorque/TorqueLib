package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueMiscUtils;

/**
 * Controls code execution based on an action and a timeout.
 *
 * @author Justus Languell
 */
public final class TorqueTimeout {
    private final double timeout, NOT_STARTED = -1;
    private double start;

    /**
     * Create a new TorqueTimeout and specify the timeout.
     *
     * @param timeout The timeout in seconds.
     */
    public TorqueTimeout(final double timeout) {
        this.timeout = timeout;
        this.start = NOT_STARTED;
    }

    /**
     * If action is true and it hasn't been true for longer than
     * the timeout, return true.
     *
     * @param action The condition to check.
     */
    public final boolean calculate(final boolean action) {
        if (!action)
            return (start = NOT_STARTED) != NOT_STARTED; // sets start to -1 and returns false
                                                         // wastes about 2-3 cpu cycles 😎
        if (start == NOT_STARTED) start = TorqueMiscUtils.time();
        return TorqueMiscUtils.time() - start < timeout;
    }
}
