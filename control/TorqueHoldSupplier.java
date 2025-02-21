/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.function.BooleanSupplier;

/**
 * Functional Wrapper for TorqueHold.
 *
 * @author Davey Adams
 */
public final class TorqueHoldSupplier {
    private final TorqueHold hold;
	private final BooleanSupplier input;

    public TorqueHoldSupplier(final BooleanSupplier input) {
		this.input = input;
        hold = new TorqueHold();
    }

    public int get() {
        return hold.calculate(input.getAsBoolean());
    }

	public final void onTrue(final Runnable callback) {
        if (get() == 1) callback.run();
    }

	public final void onFalse(final Runnable callback) {
        if (get() == 0) callback.run();
    }

    public final void onTrueOrFalse(final Runnable trueCallback, final Runnable falseCallback) {
		int value = get();
        if (value == 1) trueCallback.run();
		if (value == 0) falseCallback.run();
    }
}