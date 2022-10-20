/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueMath;

/**
 * An implementation of the valueshifter used in
 * some of the robots.ß
 *
 * @author Justus Languell
 */
public final class TorqueTraversableRange {

    private final TorqueClick clickUp = new TorqueClick();
    private final TorqueClick clickDown = new TorqueClick();
    private final TorqueClick clickMin = new TorqueClick();
    private final TorqueClick clickMax = new TorqueClick();

    private final double maximum;
    private final double minimum;
    private final double increment;

    private double value;

    /**
     * Creates a new TorquevalueSettings object with default increment of 0.1.
     *
     * @param value   The initial value setting.
     * @param minimum The maximum value setting.
     * @param maximum The minimum value setting.
     */
    public TorqueTraversableRange(final double value, final double minimum, final double maximum) {
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = .1;
    }

    /**
     * Creates a new TorquevalueSettings object.
     *
     * @param value     The initial value setting.
     * @param minimum   The maximum value setting.
     * @param maximum   The minimum value setting.
     * @param increment The increment of the value settings.
     */
    public TorqueTraversableRange(final double value, final double minimum, final double maximum,
                                  final double increment) {
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    /**
     * Updates (and optionally returns) the value setting based on the controller
     * input.
     *
     * @param up   The button mapped to incrementing value.
     * @param down The button mapped to decrementing value.
     * @param min  The button mapped to setting value to minimum.
     * @param max  The button mapped to setting value to maximum.
     *
     * @return The current value setting.
     */
    public final double update(final boolean up, final boolean down, final boolean min, final boolean max) {
        if (clickUp.calculate(up)) value = (double)TorqueMath.constrain(value + increment, minimum, maximum);
        if (clickDown.calculate(down)) value = (double)TorqueMath.constrain(value - increment, minimum, maximum);

        if (clickMin.calculate(min)) value = minimum;
        if (clickMax.calculate(max)) value = maximum;

        return value;
    }

    /**
     * Returns the current value setting.
     *
     * @return Current value setting.
     */
    public final double get() { return value; }
}
