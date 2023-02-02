/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * Class that deals with doing one thing the first time
 * the loop executes and another thing once all subsequent
 * iterations.
 *
 * @author Justus Languell
 */
public final class TorqueSubsequent {
    private boolean hasRan;

    public TorqueSubsequent() { hasRan = false; }

    /**
     * Is this the first time the loop has executed?
     *
     * @return If this is the first time the loop has executed.
     */
    public final boolean calculate() {
        if (hasRan) return false;
        return hasRan = true;
    }

    /**
     * Executes initial if this is the first time, and subsequent if
     * this is not the first time.
     *
     * @param initial The function to run on the first time.
     * @param subsequent The function to run subsequent times.
     */
    public final void execute(final Runnable initial, final Runnable subsequent) {
        if (calculate())
            initial.run();
        else
            subsequent.run();
    }

    public final void reset() { hasRan = false; }
}
