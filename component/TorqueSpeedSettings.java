package org.texastorque.torquelib.component;

import org.texastorque.torquelib.util.TorqueClick;
import org.texastorque.torquelib.util.TorqueMathUtil;

/**
 * An implementation of the speedshifter used in 
 * some of the robots. 
 * 
 * @author Justus Omar Jack
 */
public class TorqueSpeedSettings {

    private TorqueClick clickUp = new TorqueClick();
    private TorqueClick clickDown = new TorqueClick();
    private TorqueClick clickMin = new TorqueClick();
    private TorqueClick clickMax = new TorqueClick();

    private final double maximum;
    private final double minimum;
    private final double increment;

    private double speed;

    public TorqueSpeedSettings(double minimum, double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = .1;
    }

    public TorqueSpeedSettings(double minimum, double maximum, double increment) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public double update(boolean up, boolean down, boolean min, boolean max) {
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

    public double getSpeed() {
        return speed;
    }
}
