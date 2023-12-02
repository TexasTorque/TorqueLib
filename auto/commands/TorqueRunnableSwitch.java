/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.Map;
import java.util.function.BooleanSupplier;
import org.texastorque.torquelib.auto.TorqueCommand;

/**
 * TorqueSwitch
 */
public final class TorqueRunnableSwitch extends TorqueCommand {
    // PLAYING WITH NULL... THIS *CAN* BE DANGEROUS, BUT SINCE THE APPLICATION
    // IS PRETTY SIMPLE IT IS FINE.
    private TorqueRun todo = null, onElse = null;
    private final Map<BooleanSupplier, TorqueRun> options;

    public TorqueRunnableSwitch(final BooleanSupplier condition, final TorqueRun option) {
        this(Map.of(condition, option), null);
    }

    public TorqueRunnableSwitch(final BooleanSupplier condition, final TorqueRun option,
            final TorqueRun onElse) {
        this(Map.of(condition, option), onElse);
    }

    public TorqueRunnableSwitch(final Map<BooleanSupplier, TorqueRun> options) {
        this(options, null);
    }

    public TorqueRunnableSwitch(final Map<BooleanSupplier, TorqueRun> options,
            final TorqueRun onElse) {
        this.options = options;
        this.onElse = onElse;
    }

    @Override
    protected final void init() {
        for (final Map.Entry<BooleanSupplier, TorqueRun> entry : options.entrySet()) {
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
        return true;
    }

    @Override
    protected final void end() {
        if (todo != null)
            todo.reset();
    }
}
