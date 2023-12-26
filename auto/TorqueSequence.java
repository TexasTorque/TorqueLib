/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto;

import java.util.ArrayList;

import org.texastorque.torquelib.auto.commands.TorqueRunSequence;

/**
 * Texas Torque autonomous sequence base class.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Texas Torque
 */
public abstract class TorqueSequence {
    private final ArrayList<TorqueBlock> commands = new ArrayList<TorqueBlock>();
    public boolean ended = false;
    private int blockIndex = 0;

    public TorqueSequence() {
    }

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

    public final TorqueCommand command() {
        return new TorqueRunSequence(this);
    }

    protected final void addBlock(final TorqueBlock block) { commands.add(block); }

    protected final void addBlock(final TorqueCommand... commands) { addBlock(new TorqueBlock(commands)); }
}