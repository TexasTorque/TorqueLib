/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class TorqueContinuous extends TorqueCommand {
    private final Runnable command;
    private final BooleanSupplier end;

    public TorqueContinuous(final Runnable command) { 
        this(command, () -> false);
    }

    public TorqueContinuous(final Runnable command, final BooleanSupplier end) { 
        this.command = command; 
        this.end = end;
    }

    @Override
    protected final void init() {
        command.run();
    }

    @Override
    protected final void continuous() {
        command.run();
    }

    @Override
    protected final boolean endCondition() {
        return end.getAsBoolean();
    }

    @Override
    protected final void end() {}
}