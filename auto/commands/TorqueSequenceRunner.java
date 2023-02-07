/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.TorqueSequence;

public final class TorqueSequenceRunner extends TorqueCommand {
    private final TorqueSequence sequence;

    public TorqueSequenceRunner(final TorqueSequence sequence) { this.sequence = sequence; }

    @Override
    protected final void init() {}

    @Override
    protected final void continuous() {
        sequence.run();
    }

    @Override
    protected final boolean endCondition() {
        return sequence.hasEnded();
    }

    @Override
    protected final void end() {}
}
