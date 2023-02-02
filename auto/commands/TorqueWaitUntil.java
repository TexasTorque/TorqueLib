/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import edu.wpi.first.wpilibj.Timer;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class TorqueWaitUntil extends TorqueCommand {
    private final BooleanSupplier condition;

    public TorqueWaitUntil(final BooleanSupplier condition) { this.condition = condition; }

    @Override
    protected final void init() {
    }

    @Override
    protected final void continuous() {}

    @Override
    protected final boolean endCondition() {
        return condition.getAsBoolean();
    }

    @Override
    protected final void end() {}
}