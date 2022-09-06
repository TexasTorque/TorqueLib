/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

import org.texastorque.torquelib.util.TorqueMath;

/**
 * An implementation of the speedshifter used in
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

    private double speed;

    /**
     * Creates a new TorqueSpeedSettings object with default increment of 0.1.
     *
     * @param speed   The initial speed setting.
     * @param minimum The maximum speed setting.
     * @param maximum The minimum speed setting.
     */
    public TorqueTraversableRange(final double speed, final double minimum, final double maximum) {
        this.speed = speed;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = .1;
    }

    /**
     * Creates a new TorqueSpeedSettings object.
     *
     * @param speed     The initial speed setting.
     * @param minimum   The maximum speed setting.
     * @param maximum   The minimum speed setting.
     * @param increment The increment of the speed settings.
     */
    public TorqueTraversableRange(final double speed, final double minimum, final double maximum,
                                  final double increment) {
        this.speed = speed;
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    /**
     * Updates (and optionally returns) the speed setting based on the controller
     * input.
     *
     * @param up   The button mapped to incrementing speed.
     * @param down The button mapped to decrementing speed.
     * @param min  The button mapped to setting speed to minimum.
     * @param max  The button mapped to setting speed to maximum.
     *
     * @return The current speed setting.
     */
    public final double update(final boolean up, final boolean down, final boolean min, final boolean max) {
        if (clickUp.calculate(up)) speed = (double)TorqueMath.constrain(speed + increment, minimum, maximum);
        if (clickDown.calculate(down)) speed = (double)TorqueMath.constrain(speed - increment, minimum, maximum);

        if (clickMin.calculate(min)) speed = minimum;
        if (clickMax.calculate(max)) speed = maximum;

        return speed;
    }

    /**
     * Returns the current speed setting.
     *
     * @return Current speed setting.
     */
    public final double getSpeed() { return speed; }
}
