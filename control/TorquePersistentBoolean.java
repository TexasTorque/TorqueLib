/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.control;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Rolling median
 *
 * o(nlogn)
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorquePersistentBoolean {
    private final int window;
    private final Queue<Boolean> values;

    /**
     * Constructs a new rolling median class, specifying the window size.
     *
     * @param window The window size for each section (twice is the amount
     *               stored).
     */
    public TorquePersistentBoolean(final int window) {
        this.window = window;
        values = new LinkedList<>();
    }

    /**
     * Adds a value.
     *
     * @param value The value to add.
     */
    public final void add(final boolean value) {
        if (values.size() >= window) values.poll();
        values.add(value);
    }

    public final boolean any() { return any(true); }

    public final boolean any(final boolean value) { return portion(value) > 0; }

    public final boolean all() { return all(true); }

    public final boolean all(final boolean value) { return portion(value) >= 1.; }

    public final double portion() { return portion(true); }

    /**
     * Calculates the % of window that is "value".
     */
    public final double portion(final boolean value) {
        return values.stream().mapToInt(v -> v ? 1 : 0).sum() / (double)values.size();
    }
}