/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * Wrapper for an array that has a selector that can be
 * incremented or decremented on controller inputs.
 *
 * [1, 2, 3, 4, 5]
 *  <- ^ -> ß
 *
 * @author Justus Languell
 */
public final class TorqueTraversableSelection<T> {
    private final TorqueClick increment = new TorqueClick(), decrement = new TorqueClick();

    private int index = 0, lastIndex = 0;
    private final T[] values;

    public TorqueTraversableSelection(final T... values) { this.values = values; }
    public TorqueTraversableSelection(int index, final T... values) {
        this.index = index;
        this.values = values;
    }

    public final T calculate(final boolean decrement, final boolean increment) {
        if (this.decrement.calculate(decrement)) index = Math.max(index - 1, 0);
        if (this.increment.calculate(increment)) index = Math.min(index + 1, values.length - 1);
        return values[index];
    }

    /**
     * Check if the class has been updated since the last call of this method.
     *
     * @return Has the class been updated since the last call of this method.
     */
    public final boolean hasUpdated() {
        if (index == lastIndex) return false;
        return (lastIndex = index) == index;
    }

    public final T get() { return values[index]; }
    public final T get(final int index) { return values[index]; }

    public final void set(final int index) { this.index = index; }
}
