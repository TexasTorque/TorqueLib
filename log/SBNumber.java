package org.texastorque.torquelib.log;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SBNumber extends SBEntry {
    public SBNumber(final String name, final double defaultValue) {
        super(name, defaultValue);
        SmartDashboard.putNumber(name, defaultValue);
    }

    public void set(final double value) {
        SmartDashboard.putNumber(name, value);
    }

    public double get() {
        return SmartDashboard.getNumber(name, (double) defaultValue);
    } 
}
