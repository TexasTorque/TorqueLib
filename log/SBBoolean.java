package org.texastorque.torquelib.log;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SBBoolean extends SBEntry {
    public SBBoolean(final String name, final boolean defaultValue) {
        super(name, defaultValue);
        SmartDashboard.putBoolean(name, defaultValue);
    }

    public void set(final boolean value) {
        SmartDashboard.putBoolean(name, value);
    }

    public boolean get() {
        return SmartDashboard.getBoolean(name, (boolean) defaultValue);
    } 
}
