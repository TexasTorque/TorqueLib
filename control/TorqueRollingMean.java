/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Rolling mean
 *
 */
public final class TorqueRollingMean {
    private final int window;

    private final Queue<Double> med;

    /**
     * Constructs a new rolling mean class, specifying the window size.
     *
     * @param window The window size for each section (twice is the amount
     *               stored).
     */
    public TorqueRollingMean(final int window) {
        this.window = window;
        med = new LinkedList<>();
    }

    /**
     * Adds and performs calculation
     *
     * @param value The value to add.
     * @return The mean value.
     */
    public final double calculate(final double value) {
        if (med.size() >= window)
            med.poll();
        med.add(value);

        double totalVal = 0;

        for (double val : med)
            totalVal += val;

        return totalVal / med.size();
    }
}