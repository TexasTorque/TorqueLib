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
    private final TorqueClick increment = new TorqueClick();
    private final TorqueClick decrement = new TorqueClick();

    private int index, lastIndex;
    private final T[] values;

    public TorqueTraversableSelection(final T... values) { this.values = values; }

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
}
