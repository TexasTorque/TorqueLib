package org.texastorque.torquelib.util;

/**
 * A replacement for the TorqueToggle class (pretty much)
 * 
 * @author Jack Pittenger
 */
public class TorqueClick {
    private boolean lastValue;

    public TorqueClick() {
        lastValue = false;
    }

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
