/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
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

    public TorqueBlock(TorqueCommand... commands) {
        for (TorqueCommand command : commands) { add(command); }
    }

    public final void addCommand(TorqueCommand command) { add(command); }
}