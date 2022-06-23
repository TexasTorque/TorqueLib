package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class Execute extends TorqueCommand  {
    private final Runnable command;

    public Execute(final Runnable command) {
        this.command = command;
    }

    @Override
    protected final void init() { command.run(); }

    @Override
    protected final void continuous() {}

    @Override
    protected final boolean endCondition() { return true; }

    @Override
    protected final void end() {}
}