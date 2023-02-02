/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.function.BooleanSupplier;

/**
 * Functional Wrapper for TorqueToggle.
 * 
 * @author Justus Languell 
 */
public final class TorqueToggleSupplier extends TorqueBoolSupplier {
    private final TorqueToggle toggle;

    public TorqueToggleSupplier(final BooleanSupplier input) { 
        this(input, false); 
    }

    public TorqueToggleSupplier(final BooleanSupplier input, final boolean value) {
        super(input);
        toggle = new TorqueToggle(false);
    }

    @Override
    public final boolean get() { 
        toggle.calculate(input.getAsBoolean());
        return toggle.get();
    }
}
