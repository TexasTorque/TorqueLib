package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogInput;

public class TorquePotentiometer {

    private AnalogInput pot;

    private double maxValue;
    private double minValue;
    
    private double maxPosition;
    private double minPosition;

    public TorquePotentiometer(int port) {
        pot = new AnalogInput(port);
    }

    public void setInputRange(double max, double min) {
        maxValue = max;
        minValue = min;
    }
    
    public void setPositionRange(double max, double min) {
        maxPosition = max;
        minPosition = min;
    }

    public double get() {
        return (getRaw() - minValue) / (maxValue - minValue);
    }
    
    public double getPosition() {
        return get() * (maxPosition - minPosition) + minPosition;
    }

    public double getRaw() {
        return pot.getValue();
    }
    
    public double getRawVoltage() {
        return pot.getVoltage();
    }
}
