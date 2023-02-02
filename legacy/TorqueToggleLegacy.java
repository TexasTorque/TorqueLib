/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.legacy;

/**
 * Dont use this crap.
 */
@Deprecated
public class TorqueToggleLegacy {

    private boolean toggle;
    private boolean lastValue;

    public TorqueToggleLegacy() {
        toggle = false;
        lastValue = false;
    }

    public TorqueToggleLegacy(boolean override) { toggle = override; }

    public void calculate(boolean currentValue) {
        // Checks for an edge in boolean state. We only want to perform an action once
        // when we go from False to True
        if (currentValue != lastValue) {
            // If the value is true now, it is the first time it is true. Flip the toggle.
            if (currentValue) { toggle = !toggle; }

            // Keep track of the previous value. Does not need to be updated iflastCheck is
            // already equal to current.
            lastValue = currentValue;
        }
    }

    public void set(boolean override) { toggle = override; }

    public boolean get() { return toggle; }
}
