/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * This is the cool TorqueToggle.
 *
 * Use this one instead.
 */
public final class TorqueToggle {

    private final TorqueClick click;
    private boolean value;

    public TorqueToggle() { this(false); }

    public TorqueToggle(final boolean value) {
        click = new TorqueClick();
        set(value);
    }

    public final void calculate(final boolean current) {
        if (click.calculate(current)) value = !value;
    }

    public final void set(final boolean value) { this.value = value; }

    public final boolean get() { return value; }
}
