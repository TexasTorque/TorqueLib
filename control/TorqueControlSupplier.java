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
public final class TorqueControlSupplier {
    private final TorqueControl hold;
	private final BooleanSupplier input;

    public TorqueControlSupplier(final BooleanSupplier input) {
		this.input = input;
        hold = new TorqueControl();
    }

    public int get() {
        return hold.calculate(input.getAsBoolean());
    }

	public final void onStart(final Runnable callback) {
        if (get() == 1) callback.run();
    }

	public final void onEnd(final Runnable callback) {
        if (get() == 0) callback.run();
    }

	public final void whileHeld(final Runnable callback) {
        if (get() == -1) callback.run();
    }

	public final void whileNot(final Runnable callback) {
        if (get() == -2) callback.run();
    }

    public final void onStartOrEnd(final Runnable trueCallback, final Runnable falseCallback) {
		int value = get();
        if (value == 1) trueCallback.run();
		if (value == 0) falseCallback.run();
    }

    public final void onStartOrEndOrWhileOrWhileNot(final Runnable startCallback, final Runnable endCallback, final Runnable whileCallback, final Runnable whileNotCallback) {
        int value = get();
        if (value == 1) startCallback.run();
		if (value == 0) endCallback.run();
		if (value == -1) whileCallback.run();
		if (value == -2) whileNotCallback.run();
    }
}