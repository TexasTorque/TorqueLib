package org.texastorque.torquelib.control;

public final class TorqueSubsequent {
    private boolean hasRan;

    public TorqueSubsequent() {
        hasRan = false;
    }

    public final boolean calculate() {
        if (hasRan) return false;
        return this.hasRan = true;
    }

    public final void execute(final Runnable initial, final Runnable subsequent) {
        if (calculate()) initial.run();
        else subsequent.run();
    }

    public final void reset() {
        hasRan = false;
    }
}
