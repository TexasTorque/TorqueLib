/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * Notifies when a boolean value changes from false to true, and true to false

 * @author Davey Adams
 */
public final class TorqueControl {
    private boolean lastValue;

    public TorqueControl() { lastValue = false; }

    /**
     * Has current just changed from false to true or true to false?
     *
     * @param current The current value to check.
     *
     * @return 1 for false to true, 0 for true to false, -1 if the value is true and was true, -2 if the value is false and was false.
     */
    public final int calculate(final boolean current) {
		if (lastValue != current) {
			lastValue = current;
			if (current) return 1;
			return 0;
		}
        lastValue = current;
        if (current) return -1;
        return -2;
    }
}
