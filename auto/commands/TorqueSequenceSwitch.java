/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.Map;
import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.TorqueSequence;

/**
 * TorqueSwitch
 */
public final class TorqueSequenceSwitch extends TorqueCommand {
    // PLAYING WITH NULL... THIS *CAN* BE DANGEROUS, BUT SINCE THE APPLICATION
    // IS PRETTY SIMPLE IT IS FINE.
    private TorqueSequence todo = null, onElse = null;
    private final Map<BooleanSupplier, TorqueSequence> options;

    public TorqueSequenceSwitch(final BooleanSupplier condition, final TorqueSequence option) {
        this(Map.of(condition, option), null);
    }

    public TorqueSequenceSwitch(final BooleanSupplier condition, final TorqueSequence option, final TorqueSequence onElse) {
        this(Map.of(condition, option), onElse);
    }

    public TorqueSequenceSwitch(final Map<BooleanSupplier, TorqueSequence> options) {
        this(options, null);
    }

    public TorqueSequenceSwitch(final Map<BooleanSupplier, TorqueSequence> options, final TorqueSequence onElse) {
        this.options = options;
        this.onElse = onElse;
    }

    @Override
    protected final void init() {
        for (final Map.Entry<BooleanSupplier, TorqueSequence> entry : options.entrySet()) {
            if (entry.getKey().getAsBoolean()) {
                todo = entry.getValue();
                return;
            }
        }
        todo = onElse;
    }

    @Override
    protected final void continuous() {
        if (todo != null)
            todo.run();
    }

    @Override
    protected final boolean endCondition() {
        if (todo == null)
            return true;
        return todo.hasEnded();
    }

    @Override
    protected final void end() {
        if (todo != null)
            todo.reset();
    }
}
