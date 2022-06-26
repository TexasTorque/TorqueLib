package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueMiscUtil;

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
        if (!action)
            return (start = NOT_STARTED) + 1;

        if (start == NOT_STARTED) start = TorqueMiscUtil.time();
        return Math.min(slope * (TorqueMiscUtil.time() - start) + initial, end);
    }
}
