/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueWaitTime extends TorqueCommand {
    private final double time;
    private double start;
    private Runnable command;

    public TorqueWaitTime(final double time) {
        this(time, null);
    }

    public TorqueWaitTime(final double time, final Runnable command) {
        this.time = time;
        this.command = command;
    }

    @Override
    protected final void init() {
        start = Timer.getFPGATimestamp();
    }

    @Override
    protected final void continuous() {
        if (command != null)
            command.run();
    }

    @Override
    protected final boolean endCondition() {
        return (Timer.getFPGATimestamp() - start) >= time;
    }

    @Override
    protected final void end() {
    }
}