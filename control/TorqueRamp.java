/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

/**
 * Returns a certain value that linearly increases over time.
 *
 * @author Justus Languell
 */
public final class TorqueRamp {
    private final double initial, end, slope, NOT_STARTED = -1;
    private double start;

    public TorqueRamp(final double time, final double initial, final double end) {
        this.initial = initial;
        this.end = end;
        this.slope = (end - start) / time;
        this.start = NOT_STARTED;
    }

    public final double calculate(final boolean action) {
        if (!action) return (start = NOT_STARTED) + 1;

        if (start == NOT_STARTED) start = TorqueUtil.time();
        return Math.min(slope * (TorqueUtil.time() - start) + initial, end);
    }
}
