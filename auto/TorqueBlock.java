/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto;

import java.util.ArrayList;

/**
 * Basically a typedef to wrap ArrayList<TorqueCommand> with Block
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Justus
 */
public final class TorqueBlock extends ArrayList<TorqueCommand> {
    public TorqueBlock() { super(); }

    // loops might be able to have local variable marked final
    public TorqueBlock(final TorqueCommand... commands) {
        for (TorqueCommand command : commands) add(command);
    }

    public final void addCommand(final TorqueCommand command) { add(command); }
}