/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.function.BooleanSupplier;

/**
 * Designed to be extended.
 *
 * @author Justus Languell
 */
public class TorqueBoolSupplier {
    protected final BooleanSupplier input;

    public TorqueBoolSupplier(final BooleanSupplier input) {
        this.input = input;
    }

    public boolean get() {
        return input.getAsBoolean();
    }

    public final void onTrue(final Runnable callback) {
        if (get()) callback.run();
    }

    public final void onTrueOrFalse(final Runnable trueCallback, final Runnable falseCallback) {
        // if (get()) trueCallback.run();
        // else falseCallback.run();

        // Forgot you could use this?
        (get() ? trueCallback : falseCallback).run();

    }
}