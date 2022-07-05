/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

/**
 * A wrapper to toggle a variable based on the output of a TorqueClick.
 *
 * @author Justus Languell
 */
public final class TorqueToggle {
    private final TorqueClick click;
    private boolean value;

    public TorqueToggle() { 
        this.click = new TorqueClick();
    }
    
    public TorqueToggle(final boolean value) {
        this.click = new TorqueClick(value);
        this.value = value;
    }

    public final void calculate(final boolean toggle) {
        if (click.calculate(toggle)) value = !value;
    }

    public final boolean get() { return value; }
}
