package org.texastorque.torquelib.base;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A enum interface that is extended by enums that 
 * describe subsystem state.
 */
public interface TorqueSubsystemState {
    /**
     * Logs an enum that implements TorqueSubsystemState to SmartDashboard.
     * 
     * @param state The state enum.
     */
    public static void logState(final TorqueSubsystemState state) {
        SmartDashboard.putString(state.getClass().getSimpleName(), state.toString());
    }
}
