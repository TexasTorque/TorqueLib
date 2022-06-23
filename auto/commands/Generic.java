package org.texastorque.torquelib.auto.commands;

import java.util.function.Supplier;

import org.texastorque.torquelib.auto.TorqueCommand;

public final class Generic extends TorqueCommand  {
    private final Runnable onInit, onContinuous, onEnd;
    private final Supplier<Boolean> condition;

    public Generic(final Builder builder) {
        this.onInit = builder.init;
        this.onContinuous = builder.continuous;
        this.onEnd = builder.end;
        this.condition = builder.condition;
    }

    @Override
    protected final void init() { if (onInit != null) onInit.run(); }

    @Override
    protected final void continuous() { if (onContinuous != null) onContinuous.run(); }

    @Override
    protected final boolean endCondition() { return condition == null ? true : condition.get(); }

    @Override
    protected final void end() { if (onEnd != null) onEnd.run(); }

    public static final Builder useBuilder() { return new Builder(); }

    public static final class Builder {
        private Runnable init, continuous, end;
        private Supplier<Boolean> condition;

        public Builder() {}

        public final Builder onInit(final Runnable init) {
            this.init = init; return this;
        }

        public final Builder onContinuous(final Runnable continuous) {
            this.continuous = continuous; return this;
        }

        public final Builder onEnd(final Runnable end) {
            this.end = end; return this;
        }

        public final Builder addCondition(final Supplier<Boolean> condition) {
            this.condition = condition; return this;
        }

        public final Generic build() { return new Generic(this); }
    }

   
}