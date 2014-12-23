package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogInput;

public class TorquePotentiometer {

    private AnalogInput pot;

    private double maxVoltage;
    private double minVoltage;

    public TorquePotentiometer(int port) {
        pot = new AnalogInput(port);
    }

    public void setRange(double max, double min) {
        maxVoltage = max;
        minVoltage = min;
    }

    public double get() {
        return 1 - limitValue((getRaw() - minVoltage) / (maxVoltage - minVoltage));
    }

    private double limitValue(double value) {
        if (value > 1.0) {
            return 1.0;
        } else {
            return value;
        }
    }

    public double getRaw() {
        return pot.getVoltage();
    }

}
