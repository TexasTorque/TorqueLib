/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

/**
 * Common subsystem interface for TorqueRobotBase.
 * 
 * @author Justus Languell
 */
public interface TorqueSubsystem {
    public abstract void initialize(final TorqueMode mode);

    public abstract void update(final TorqueMode mode);

    // Overridable by the TorqueSubsystem baseclass
    public abstract void run(final TorqueMode mode);

    public abstract void clean(final TorqueMode mode);
}
