/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;

import org.texastorque.torquelib.auto.TorqueCommand;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueWaitUntil extends TorqueCommand {
    private final BooleanSupplier condition;
    private final DoubleConsumer timeConsumer;
    private double startTime;

    public TorqueWaitUntil(final BooleanSupplier condition) { 
        this(condition, (final double t) -> {} ); 
    }

    public TorqueWaitUntil(final BooleanSupplier condition, final DoubleConsumer timeConsumer) { 
        this.condition = condition; 
        this.timeConsumer = timeConsumer;
    }

    @Override
    protected final void init() {
        startTime = Timer.getFPGATimestamp();
    }

    @Override
    protected final void continuous() {
        reportTime();
    }

    @Override
    protected final boolean endCondition() {
        return condition.getAsBoolean();
    }

    @Override
    protected final void end() {
        reportTime();
    }

    private void reportTime() {
        timeConsumer.accept(Timer.getFPGATimestamp() - startTime);
    }
}