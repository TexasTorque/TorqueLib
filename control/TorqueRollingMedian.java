/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Rolling median
 *
 * o(nlogn)
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueRollingMedian {
    private final int window;

    private final Queue<Double> med;

    /**
     * Constructs a new rolling median class, specifying the window size.
     *
     * @param window The window size for each section (twice is the amount
     *               stored).
     */
    public TorqueRollingMedian(final int window) {
        this.window = window;
        med = new LinkedList<>();
    }

    /**
     * Adds and performs calculation
     *
     * @param value The value to add.
     * @return The median value.
     */
    public final double calculate(final double value) {
        if (med.size() >= window) med.poll();
        med.add(value);

        Double[] vals = med.toArray(new Double[0]);
        Arrays.sort(vals);

        if (vals.length % 2 == 0)
            return (vals[vals.length / 2] + vals[vals.length / 2 - 1]) / 2.;
        else
            return vals[vals.length / 2];
    }
}