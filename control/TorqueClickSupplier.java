/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.function.BooleanSupplier;

/**
 * Functional Wrapper for TorqueClick.
 *
 * @author Justus Languell
 */
public final class TorqueClickSupplier extends TorqueBoolSupplier {
    private final TorqueClick click;

    public TorqueClickSupplier(final BooleanSupplier input) {
        super(input);
        click = new TorqueClick();
    }

    @Override
    public boolean get() {
        return click.calculate(input.getAsBoolean());
    }
}