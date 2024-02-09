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

public final class TorqueWhile extends TorqueCommand {
    private final TorqueSequence sequence;
    private final BooleanSupplier condition;
    private boolean endNow = false;

    public TorqueWhile(final BooleanSupplier condition, final TorqueSequence sequence) {
        this.sequence = sequence;
        this.condition = condition;
    }

    @Override
    protected final void init() {
        endNow = !condition.getAsBoolean();
    }

    @Override
    protected final void continuous() {
        if (sequence.hasEnded())
            sequence.reset();
        sequence.run();
    }

    @Override
    protected final boolean endCondition() {
        return sequence.hasEnded() && !condition.getAsBoolean() || endNow;
    }

    @Override
    protected final void end() {
        sequence.reset();
    }
}
