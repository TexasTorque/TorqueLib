/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.auto.test;

import org.texastorque.torquelib.auto.commands.Generic;

public final class Test {
    public static final void main(final String[] arguments) {
        Generic command = Generic.useBuilder()
                                  .onInit(() -> { System.out.println("INIT"); })
                                  .onContinuous(() -> { System.out.println("CONT"); })
                                  .onEnd(() -> { System.out.println("END"); })
                                  .addCondition(() -> { return true; })
                                  .build();
    }
}
