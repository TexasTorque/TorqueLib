package org.texastorque.torquelib.log;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SBBoolean extends SBEntry {
    public SBBoolean(final String name, final boolean defaultValue) {
        super(name, defaultValue);
        SmartDashboard.putBoolean(name, defaultValue);
    }

    public final void set(final boolean value) { SmartDashboard.putBoolean(name, value); }

    public final boolean get() { return SmartDashboard.getBoolean(name, (boolean)defaultValue); }
}
