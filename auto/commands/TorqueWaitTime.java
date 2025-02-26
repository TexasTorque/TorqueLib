/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.DoubleSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueWaitTime extends TorqueCommand {
    private final DoubleSupplier timeSupplier;
    private double time;

    private Timer timer;
    private Runnable command;

    public TorqueWaitTime(final double time) {
        this(() -> time);
    }

    public TorqueWaitTime(final DoubleSupplier timeSupplier) {
        this(timeSupplier, null);
    }


    public TorqueWaitTime(final double time, final Runnable command) {
        this(() -> time, command);
    }

    public TorqueWaitTime(final DoubleSupplier timeSupplier, final Runnable command) {
        this.timeSupplier = timeSupplier;
        this.command = command;
        this.time = 0;
        this.timer = new Timer();
    }

    @Override
    protected final void init() {
        time = timeSupplier.getAsDouble();
        timer.restart();
    }

    @Override
    protected final void continuous() {
        if (command != null)
            command.run();
    }

    @Override
    protected final boolean endCondition() {
        return timer.hasElapsed(time);
    }

    @Override
    protected final void end() {
    }
}