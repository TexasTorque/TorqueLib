package org.texastorque.torquelib.log;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SBString extends SBEntry {
    public SBString(final String name, final String defaultValue) {
        super(name, defaultValue);
        SmartDashboard.putString(name, defaultValue);
    }

    public void set(final String value) {
        SmartDashboard.putString(name, value);
    }

    public String get() {
        return SmartDashboard.getString(name, (String) defaultValue);
    } 
}
