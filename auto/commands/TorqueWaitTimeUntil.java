/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueWaitTimeUntil extends TorqueCommand {
    private final BooleanSupplier condition;
	private Timer timer;
	private double time;

    public TorqueWaitTimeUntil(final double time, final BooleanSupplier condition) {
		this.time = time;
        this.condition = condition;
		this.timer = new Timer();
    }

    @Override
    protected final void init() {
		timer.restart();
    }

    @Override
    protected final void continuous() {
        
    }

    @Override
    protected final boolean endCondition() {
        return timer.hasElapsed(time) || condition.getAsBoolean();
    }

    @Override
    protected final void end() {
        
    }
}