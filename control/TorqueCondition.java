/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.function.Consumer;
import org.texastorque.torquelib.util.TorqueMath;

/**
 * A replacement for the if else statments to check for conditions
 * that determine the object to pass to a consumer function.
 *
 * Is about 30-50% slower than using chained if else statements,
 * but can be more readable.
 *
 * @author Justus Languell
 */
public final class TorqueCondition<T> {
    private T value = null;

    /**
     * Creates a new chainable instance of the condition.
     *
     * @param <T> The type of the determined value.
     *
     * @return A new conditional chain.
     */
    public static final <T> TorqueCondition<T> start() { return new TorqueCondition<>(); }

    /**
     * Resets the conditional chain.
     *
     * @return The conditional chain.
     */
    public final TorqueCondition<T> reset() {
        this.value = null;
        return this;
    }

    /**
     * A wrapper for {@link TorqueCondition#checkIfElse()}, checks a condition and
     * sets the value if the internal value has yet to be set (else if logic).
     *
     * @param condition The condition.
     * @param value The value.
     *
     * @return The conditional chain.
     */
    public final TorqueCondition<T> check(final boolean condition, final T value) {
        if (condition && this.value == null) this.value = value;
        return this;
    }

    /**
     * Creates a new chainable instance of the condition and checks a condition
     * to determine if it should set the value.
     *
     * @param <T> The type of the determined value.
     * @param condition The condition.
     * @param value The value.
     *
     * @return The conditional chain.
     */
    public static final <T> TorqueCondition<T> start(final boolean condition, final T value) {
        return TorqueCondition.<T>start().check(condition, value);
    }

    /**
     * Checks a condition and sets the value regardless (if logic).
     *
     * @param condition The condition.
     * @param value The value.
     *
     * @return The conditional chain.
     */
    public final TorqueCondition<T> checkIf(final boolean condition, final T value) {
        if (condition) this.value = value;
        return this;
    }

    /**
     * Checks a condition and sets the value if the internal value has yet to be set (else if logic).
     *
     * @param condition The condition.
     * @param value The value.
     *
     * @return The conditional chain.
     */
    public final TorqueCondition<T> checkElseIf(final boolean condition, final T value) {
        if (condition && this.value == null) this.value = value;
        return this;
    }

    /**
     * A value to set if the value has yet to be set (else logic).
     *
     * @param condition The condition.
     * @param value The value.
     *
     * @return The conditional chain.
     */
    public final TorqueCondition<T> checkElse(final T value) {
        if (this.value == null) this.value = value;
        return this;
    }

    /**
     * Get the internal value.
     *
     * @return The internal value.
     */
    public final T get() { return value; }

    /**
     * If the internal value is not null, pass it to a consumer function.
     *
     * @param consumer The consumer function.
     */
    public final void and(final Consumer<T> consumer) {
        if (this.value != null) consumer.accept(this.value);
    }

    public static final void main(final String[] arguments) throws Exception {
        final long r = TorqueMath.random(0, 3);

        TorqueCondition.<String>start()
                .check(r == 0, "a")
                .check(r == 1, "b")
                .check(r == 2, "c")
                .check(r == 3, "d")
                .and(System.out::println);
    }
}
