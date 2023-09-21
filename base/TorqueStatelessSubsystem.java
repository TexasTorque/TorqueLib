/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

/**
 * Template for non-state-based subsystems.
 *
 * @author Justus Languell
 */
public abstract class TorqueStatelessSubsystem implements TorqueSubsystem {

    public abstract void initialize(final TorqueMode mode);

    public abstract void update(final TorqueMode mode);

    public final void run(final TorqueMode mode) {
        update(mode);
    }
}
