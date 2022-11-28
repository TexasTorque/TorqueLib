/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

/**
 * This class implements a slew rate limiter. In a nutshell, this limits the
 * rate-of-change of the units to avoid sharp movement acceleration while still
 * providing fine control.
 *
 * @author Jack Pittenger
 */
public final class TorqueSlewLimiter {
    private final double limitAsc, limitDesc;

    private double lastVal, lastTime;

    /**
     * Creates a new TorqueSlewLimiter with the ascending and descending limit the
     * same.
     *
     * @param limit The max units-per-second
     */
    public TorqueSlewLimiter(final double limit) {
        this.limitAsc = limit;
        this.limitDesc = limit;
    }

    /**
     * Creates a new TorqueSlewLimiter with a different ascending and descending
     * limit.
     *
     * @param limitAsc  The max units-per-second increasing absolutely
     * @param limitDesc The max units-per-second descending absolutely
     */
    public TorqueSlewLimiter(final double limitAsc, final double limitDesc) {
        this.limitAsc = limitAsc;
        this.limitDesc = limitDesc;
    }

    /**
     * @param val The requested input
     * @return The limited value
     */
    public final double calculate(final double val) {
        final double t = TorqueUtil.time();
        lastVal += Math.signum(val - lastVal) *
                   Math.min(Math.abs(val) > Math.abs(lastVal) ? limitAsc : limitDesc * t - lastTime,
                            Math.abs(val - lastVal));
        lastTime = t;
        return lastVal;
    }

    /**
     * @param val The requested input
     * @param use For testing
     * @return The limited value
     */
    public final double calculate(final double val, final boolean use) { return use ? calculate(val) : val; }

    // /**
    //  * @param val The requested input
    //  * @return The limited value
    //  */
    // public double calculate(double val) {
    //     double t = TorqueUtil.time();
    //     double dt = t - lastTime;
    //     double dx = val - lastVal;

    //     // ascending
    //     if (Math.abs(val) > Math.abs(lastVal)) {
    //         lastVal += Math.signum(dx) * Math.min(limitAsc * dt, Math.abs(dx));
    //     } else { // descending
    //         lastVal += Math.signum(dx) * Math.min(limitDesc * dt, Math.abs(dx));

    //     }

    //     lastTime = t;

    //     return lastVal;
    // }
}
