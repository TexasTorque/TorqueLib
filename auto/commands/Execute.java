/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class Execute extends TorqueCommand {
    private final Runnable command;

    public Execute(final Runnable command) { this.command = command; }

    @Override
    protected final void init() {
        command.run();
    }

    @Override
    protected final void continuous() {}

    @Override
    protected final boolean endCondition() {
        return true;
    }

    @Override
    protected final void end() {}
}