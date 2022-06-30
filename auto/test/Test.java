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
