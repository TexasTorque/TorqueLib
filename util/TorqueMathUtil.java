package org.texastorque.torquelib.util;

/**
 * A static class of useful math utilities.
 *
 * @author Justus Languell
 */
public class TorqueMathUtil {
    private TorqueMathUtil() {}

    /**
     * Return contrained value n between a and -a
     *
     * @param n Value to be constrained
     * @param a Value to constrain by
     * @return The constrained value of n
     */
    public static double constrain(final double n, final double a) { return Math.max(Math.min(n, a), -a); }

    /**
     * Return contrained value n between a and b
     *
     * @param n Value to be constrained
     * @param a Value to constrain the value over, the minimum value
     * @param b Value to constrain the value under, the maximum value
     * @return The constrained value of n
     */
    public static double constrain(final double n, final double a, final double b) {
        return Math.max(Math.min(n, b), a);
    }
}