/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

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
        if (start == NOT_STARTED) start = TorqueUtil.time();
        return TorqueUtil.time() - start < timeout;
    }
}
