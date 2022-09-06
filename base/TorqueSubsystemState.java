/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.base;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.texastorque.torquelib.util.TorqueLogging;

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
        // TorqueLogging.putString(state.getClass().getSimpleName(), state.toString());
    }
}
