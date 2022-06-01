package org.texastorque.torquelib.control;

/**
 * Notifies when a boolean value initially changes from false to true.

 * @author Jack Pittenger
 */
public final class TorqueClick {
    private boolean lastValue;

    public TorqueClick() { lastValue = false; }

    public boolean calc(final boolean current) {
        if (current) {
            if (lastValue != current) {
                lastValue = current;
                return true;
            }
        }
        lastValue = current;
        return false;
    }
}
