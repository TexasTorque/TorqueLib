package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogInput;

public class TorquePotentiometer {

    private AnalogInput pot;

    private boolean firstCycle;
    private double prevVoltage;
    private double maxVoltage;
    private double minVoltage;

    public TorquePotentiometer(int port) {
        pot = new AnalogInput(port);
        firstCycle = true;
    }

    public void setRange(double max, double min) {
        maxVoltage = max;
        minVoltage = min;
    }

    public double get() {
        return 1 - limitValue((getRaw() - minVoltage) / (maxVoltage - minVoltage));
    }

    public void reset() {
        firstCycle = true;
    }

    public void run() {
        double temp = pot.getVoltage();
        if (!firstCycle && Math.abs(temp - prevVoltage) > 4.8) {
            if (prevVoltage > 4.8) {
                temp = 5 + temp;
            } else if (prevVoltage < 0.2) {
                temp = temp - 5;
            }
        } else {
            firstCycle = false;
            prevVoltage = temp;
        }
        prevVoltage = temp;
    }

    public double getRaw() {
//        if (prevVoltage == 0.0) {
//            prevVoltage = pot.getVoltage();
//        }
//        return prevVoltage;

        return pot.getVoltage();
    }

    private double limitValue(double value) {
        if (value > 1.0) {
            return 1.0;
        } else {
            return value;
        }
    }

    public double getRawNoRollover() {
        return pot.getVoltage();
    }

}
