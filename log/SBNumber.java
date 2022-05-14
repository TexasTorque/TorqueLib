package org.texastorque.torquelib.log;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SBNumber extends SBEntry {
    public SBNumber(final String name, final double defaultValue) {
        super(name, defaultValue);
        SmartDashboard.putNumber(name, defaultValue);
    }

    public final void set(final double value) { SmartDashboard.putNumber(name, value); }

    public final double get() { return SmartDashboard.getNumber(name, (double)defaultValue); }
}
