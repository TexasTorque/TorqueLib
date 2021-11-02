package org.texastorque.torquelib.component;

import java.util.ArrayList;

import org.texastorque.torquelib.util.TorqueMathUtil;

/**
 * This class abstracts the implementation of variable speed
 * multipliers. Will implement some time later as it is not
 * very high priority.
 * 
 * Nvm, I wrote a test case and I just implemented it in Input.
 * I'd be supprised if it doesn't work at this point.
 * 
 * @author Justus
 * @author Omar 
 */
public class TorqueSpeedSettings {
    private ArrayList<Double> speeds;
    private int currentSpeedIndex;

    public TorqueSpeedSettings(double... speeds) {
        this.speeds = new ArrayList<Double>();
        for (double speed : speeds) this.speeds.add(speed);
        currentSpeedIndex = 0;
    }

    public void addSpeed(double speed) {
        this.speeds.add(speed);
    }

    public void up() { updateIndex(1); } 

    public void down() { updateIndex(-1); }

    private void updateIndex(int i) {
        currentSpeedIndex = (int) TorqueMathUtil
                .constrain(currentSpeedIndex += i, 
                0, speeds.size() - 1); 
    }

    public double getSpeedSetting() {
        return speeds.get(currentSpeedIndex);
    }
} 