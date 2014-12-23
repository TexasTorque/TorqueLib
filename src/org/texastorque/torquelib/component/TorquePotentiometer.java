package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogInput;

public class TorquePotentiometer {

    private AnalogInput pot;

    private double maxValue;
    private double minValue;

    public TorquePotentiometer(int port) {
        pot = new AnalogInput(port);
    }

    public void setRange(double max, double min) {
        maxValue = max;
        minValue = min;
    }

    public double get() {
        return 1 - limit((getRaw() - minValue) / (maxValue - minValue));
    }

    private double limit(double value) {
        if (value > 1.0) {
            return 1.0;
        } else {
            return value;
        }
    }

    public double getRaw() {
        return pot.getValue();
    }
    
    public double getRawVoltage() {
        return pot.getVoltage();
    }
}
