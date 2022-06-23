package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class Generic extends TorqueCommand  {
    private final Runnable onInit, onContinuous, onEnd;
    private final BooleanSupplier condition;

    public Generic(final Runnable onInit, final Runnable onContinuous, 
            final BooleanSupplier condition, final Runnable onEnd) {
        this.onInit = onInit;
        this.onContinuous = onContinuous;
        this.condition = condition;
        this.onEnd = onEnd;
    }

    @Override
    protected final void init() { onInit.run(); }

    @Override
    protected final void continuous() { onContinuous.run(); }

    @Override
    protected final boolean endCondition() { return condition.getAsBoolean(); }

    @Override
    protected final void end() { onEnd.run(); }
}