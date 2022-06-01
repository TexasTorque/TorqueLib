package org.texastorque.torquelib.control;

/**
 * Notifies when a boolean value initially changes from false to true.

 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueClick {
    private boolean lastValue;

    public TorqueClick() { lastValue = false; }

    /**
     * Has current just changed from false to true?
     * 
     * @param current The current value to check.
     * 
     * @return If the current value has just changed from false to true.
     */
    public final boolean calculate(final boolean current) {
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
