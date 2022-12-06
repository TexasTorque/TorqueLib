/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

/**
 * Request a time with set() and have calculate() return true
 * until the requested time has elapsed from when it was set.
 *
 * Based on the legacy TimedTruthy.
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueRequestableTimeout {
    public double requested = 0, last = 0;
    public TorqueRequestableTimeout() {}

    /**
     * Set a timeout and start the clock!
     *
     * @param requested The timeout requested.
     */
    public final void set(final double requested) {
        this.requested = Math.max(this.requested, requested);
        last = TorqueUtil.time();
    }

    /**
     * Has the timeout yet to elapse?
     *
     * @return If the timeout has yet to elapse or not.
     */
    public final boolean calculate() {
        if (requested <= 0 || last <= 0) return false;
        final double current = TorqueUtil.time();
        requested -= current - last;
        last = current;
        return requested > 0;
    }
}

/*
>Open crib
>Got ⛽
>Can scoop lmkkk
>I got sum alc too
*/