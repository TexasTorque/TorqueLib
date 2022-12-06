/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToDoubleFunction;

/**
 * A static class of useful math utilities.
 *
 * @author Justus Languell
 */
public final class TorqueMath {
    private TorqueMath() { TorqueUtil.staticConstructor(); }

    /**
     * Return contrained value n between a and -a
     *
     * @param n Value to be constrained
     * @param a Value to constrain by
     * @return The constrained value of n
     */
    public static final double constrain(final double n, final double a) { return Math.max(Math.min(n, a), -a); }

    /**
     * Return contrained value n between a and b
     *
     * @param n Value to be constrained
     * @param a Value to constrain the value over, the minimum value
     * @param b Value to constrain the value under, the maximum value
     * @return The constrained value of n
     */
    public static final double constrain(final double n, final double a, final double b) {
        return Math.max(Math.min(n, b), a);
    }

    //  /**
    //  * Return deadbanded value n between a and b
    //  *
    //  * @param n Value to be deadbanded
    //  * @param a Value to deadband the value over, the minimum value
    //  * @param b Value to deadband the value under, the maximum value
    //  * @return The deadband output
    //  */
    public static final double scaledDeadband(final double value, final double scale) {
        return Math.abs(value) < scale ? 0 : (value - (Math.abs(value) / value) * scale) / (1. - scale);
    }

    /**
     * Round number to a place value.
     *
     * @param num Number to round.
     * @param dec Place value (decimal places) to round to.
     *
     * @return The rounded number
     */
    public static final double round(final double num, final long dec) {
        return (long)Math.round((num * Math.pow(10, dec))) / Math.pow(10, dec);
    }

    /**
     * Check if an integer (as a long) is prime or not.
     *
     * @param params The number to check for prime.
     *
     * @return If the numbers if prime or not.
     */
    public static final boolean prime(final long n) {
        if (n == 2 || n == 3) return true;
        if (n < 2 || n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 6L; i <= ((long)Math.sqrt(n) + 1); i += 6)
            if (n % (i - 1) == 0 || n % (i + 1) == 0) return false;
        return true;
    }

    /**
     * Check if a number is inside of a certain bounds (inclusive).
     *
     * @param n The number to check.
     * @param a The lower bound.
     * @param b The upper bound.
     *
     * @return If the number is inside the bounds.
     */
    public static final boolean constrained(final double n, final double a, final double b) { return n >= a && n <= b; }

    /**
     * Check if a number is within a tolerance of 0.
     *
     * @param x One number.
     * @param t The tolerance.
     *
     * @return If the numbers are within tolerance.
     */
    public static final boolean toleranced(final double x, final double t) { return Math.abs(x) <= t; }

    /**
     * Check if a number is within a tolerance of another number.
     *
     * @param x One number.
     * @param y The other number.
     * @param t The tolerance.
     *
     * @return If the numbers are within tolerance.
     */
    public static final boolean toleranced(final double x, final double y, final double t) {
        return y - t <= x && x <= y + t;
    }

    /**
     * Another approach to 3 param toleranced, the one used in 2
     * param toleranced. Should do the same thing?
     *
     * @param x One number.
     * @param y The other number.
     * @param t The tolerance.
     *
     * @return If the numbers are within tolerance.
     */
    public static final boolean toleranced2(final double x, final double y, final double t) {
        return Math.abs(x - y) <= t;
    }

    /**
     * Check if a number is within a tolerance of another number with independent
     * sides.
     *
     * @param x One number.
     * @param y The other number.
     * @param u The upper tolerance.
     * @param l The lower tolerance.
     *
     * @return If the numbers are within tolerance.
     */
    public static final boolean toleranced(final double x, final double y, final double u, final double l) {
        return y - l <= x && x <= y + u;
    }

    /**
     * Returns either requestedSpeed or zero depending on if it will keep
     * currentPosition
     * between minPosition and maxPosition.
     *
     * @param requestedSpeed  The requested speed.
     * @param currentPosition The current position.
     * @param minPosition     The minimum position.
     * @param maxPosition     The maximum position.
     *
     * @return The desired speed.
     */
    public static final double linearConstraint(final double requestedSpeed, final double currentPosition,
                                                final double minPosition, final double maxPosition) {
        return (currentPosition <= minPosition && requestedSpeed < 0) ? 0
        : (currentPosition >= maxPosition && requestedSpeed > 0)      ? 0
                                                                      : requestedSpeed;
    }

    /**
     * Returns an array with range of [min, max] and steps of increment.
     *
     * @param min       Min value (inclusive).
     * @param max       Max value (inclusive).
     * @param increment Step increment.
     * @return The array in range.
     */
    public static final double[] range(final double min, final double max, final double increment) {
        final double[] array = new double[(int)((max - min) / increment) + 1];
        for (double i = min; i <= max; i += increment) array[(int)((i - min) / increment)] = i;
        return array;
    }

    /**
     * Returns an array with range of [min, max] and steps of 1.
     *
     * @param min Min value (inclusive).
     * @param max Max value (inclusive).
     * @return The array in range.
     */
    public static final double[] range(final double min, final double max) { return range(min, max, 1); }

    /**
     * This method will return an integer (as a long) in the range [1, high].
     *
     * @param high The upper bound of range the number could be from.
     *
     * @return The randomly generated number.
     */
    public static final long random(final long high) { return random(1, high); }

    /**
     * This method will return an integer (as a long) in the range [low, high].
     *
     * @param low  The lower bound range the number could be from.
     * @param high The upper bound of range the number could be from.
     *
     * @return The randomly generated number.
     */
    public static final long random(final long low, final long high) {
        // return (long) (Math.random() * (high - low + 1)) + low;
        // ThreadLocalRandom is more efficient
        return ThreadLocalRandom.current().nextLong(low, high + 1);
    }

    /**
     * Takes the average of a list of numbers.
     *
     * @param list The list of numbers.
     *
     * @return The average of the list.
     */
    public static final double average(final List<? extends Number> list) {
        return average(list, t -> t.doubleValue());
    }

    /**
     * Takes the average of the mapped functional outputs on a list of objects.
     *
     * @param list     The list of numbers.
     * @param function The function to generate the number.
     *
     * @return The average of the list.
     */
    public static final <T> double average(final List<T> list, final ToDoubleFunction<T> function) {
        return list.stream().mapToDouble(function).average().getAsDouble();
    }

    public static final void main(String[] args) throws Exception {}
}