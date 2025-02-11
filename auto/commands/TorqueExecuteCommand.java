/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;

import edu.wpi.first.wpilibj2.command.Command;

public final class TorqueExecuteCommand extends TorqueCommand {
    private final Command command;

    public TorqueExecuteCommand(final Command command) { this.command = command; }

    @Override
    protected final void init() {
        command.initialize();
    }

    @Override
    protected final void continuous() {
        command.execute();
    }

    @Override
    protected final boolean endCondition() {
        return command.isFinished();
    }

    @Override
    protected final void end() {
        command.end(false);
    }
}