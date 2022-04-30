package org.texastorque.torquelib.util;

/**
 * An implementation of the speedshifter used in
 * some of the robots.
 * 
 * @author Justus Languell 
 */
public class TorqueSpeedSettings {

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
    public TorqueSpeedSettings(final double speed, final double minimum, final double maximum) {
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
    public TorqueSpeedSettings(final double speed, final double minimum, final double maximum, final double increment) {
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
    public double update(final boolean up, final boolean down, final boolean min, final boolean max) {
        if (clickUp.calc(up))
            speed = (double) TorqueMathUtil.constrain(speed + increment, minimum, maximum);
        if (clickDown.calc(down))
            speed = (double) TorqueMathUtil.constrain(speed - increment, minimum, maximum);

        if (clickMin.calc(min))
            speed = minimum;
        if (clickMax.calc(max))
            speed = maximum;

        return speed;
    }

    /**
     * Returns the current speed setting.
     * 
     * @return Current speed setting.
     */
    public double getSpeed() {
        return speed;
    }
}
