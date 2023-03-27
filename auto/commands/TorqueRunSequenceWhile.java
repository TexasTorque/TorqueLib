/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.TorqueSequence;

public final class TorqueRunSequenceWhile extends TorqueCommand {
    private final TorqueSequence sequence;
    private final BooleanSupplier condition;

    public TorqueRunSequenceWhile(final TorqueSequence sequence, final BooleanSupplier condition) { 
        this.sequence = sequence; 
        this.condition = condition;
    }

    @Override
    protected final void init() {}

    @Override
    protected final void continuous() {
        if (sequence.hasEnded() && condition.getAsBoolean()) 
            sequence.reset();
        sequence.run();
    }

    @Override
    protected final boolean endCondition() {
        return !condition.getAsBoolean();
    }

    @Override
    protected final void end() {}
}