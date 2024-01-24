/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

import org.texastorque.torquelib.auto.commands.TorqueRun;

/**
 * State based subsystem template.
 *
 * @author Justus Languell
 */
public abstract class TorqueStatorSubsystem<T extends TorqueState> implements TorqueSubsystem {
    protected T desiredState;
    protected T lastState;

    protected TorqueStatorSubsystem(final T desiredState) {
        this(desiredState, desiredState);
    }

    protected TorqueStatorSubsystem(final T desiredState, final T lastState) {
        this.desiredState = desiredState;
        this.lastState = lastState;
    }

    public final void setState(final T state) {
        this.desiredState = state;
        if (desiredState != lastState)
            onStateChange();
    }

    public final TorqueRun yieldState(final T state) {
        return new TorqueRun(() -> setState(state));
    }

    public final T getState() {
        return this.desiredState;
    }

    public boolean wantsState(final T state) {
        return getState() == state;
    }

    public abstract void initialize(final TorqueMode mode);

    public abstract void update(final TorqueMode mode);

    public final void run(final TorqueMode mode) {
        update(mode);
        lastState = desiredState;
    }

    protected void onStateChange() {}
}
