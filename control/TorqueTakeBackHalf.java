/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueUtil;

/**
 * Simple "Take Back Half" controller algorithm implementation.
 *
 * Useful in flywheels maybe?
 *
 * @author Justus Languell
 */
public final class TorqueTakeBackHalf {
    private final double gain;
    private double tbh = 0, prevErr = 0, output = 0, rate = -1, prevTime = -1; // rate = ∆time

    public TorqueTakeBackHalf(final double gain) { this.gain = gain; }

    public final double calculate(final double error) {
        output += gain * error * updateRate();
        if ((error < 0) != (prevErr < 0)) {
            output += tbh;
            output *= 0.5;
            // output = (output + tbh) / 2
            tbh = output;
            prevErr = error;
        }
        return output;
    }

    public final double updateRate() {
        final double currTime = TorqueUtil.time();
        if (prevTime != -1) rate = currTime - prevTime;
        prevTime = currTime;
        return rate;
    }
}
