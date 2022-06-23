package org.texastorque.torquelib.auto.commands;

import java.util.function.Supplier;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class Generic extends TorqueCommand  {
    private final Runnable onInit, onContinuous, onEnd;
    private final Supplier<Boolean> condition;

    public Generic(final Runnable onInit, final Runnable onContinuous, 
            final Supplier<Boolean> condition, final Runnable onEnd) {
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
    protected final boolean endCondition() { return condition.get(); }

    @Override
    protected final void end() { onEnd.run(); }
}