/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.auto;

import java.util.ArrayList;

/**
 * Texas Torque autonomous sequence base class.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Texas Torque
 */
public abstract class TorqueSequence {
    private final ArrayList<TorqueBlock> commands = new ArrayList<TorqueBlock>();
    private boolean ended = false;
    private int blockIndex = 0;

    public TorqueSequence() {}

    protected final void addBlock(final TorqueBlock block) { commands.add(block); }

    protected final void addBlock(final TorqueCommand... commands) { addBlock(new TorqueBlock(commands)); }

    // loops might be able to have local variable marked final
    public final void run() {
        if (blockIndex < commands.size()) {
            boolean blockEnded = true;
            for (TorqueCommand command : commands.get(blockIndex))
                if (!command.run()) blockEnded = false;
            if (blockEnded) blockIndex++;
        } else if (!ended)
            ended = true;
    }

    public final boolean hasEnded() { return ended; }

    public final void reset() {
        ended = false;
        blockIndex = 0;
        for (TorqueBlock block : commands)
            for (TorqueCommand command : block) command.reset();
    }

    public final void resetBlock() {
        ended = false;
        for (TorqueCommand command : commands.get(blockIndex)) command.reset();
    }
}