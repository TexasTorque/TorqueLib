/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;
import org.texastorque.torquelib.auto.TorqueCommand;

public final class TorqueRunWhile extends TorqueCommand {
    private final TorqueCommand command;
    private final BooleanSupplier condition;

    public TorqueRunWhile(final TorqueCommand command, final BooleanSupplier condition) {
        this.command = command;
        this.condition = condition;
    }

    @Override
    protected final void init() {}

    @Override
    protected final void continuous() {
        if (!condition.getAsBoolean())
            command.reset();
        else
            command.run();
    }

    @Override
    protected final boolean endCondition() {
        return !condition.getAsBoolean();
    }

    @Override
    protected final void end() {
        command.reset();
    }
}
