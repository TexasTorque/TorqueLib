package org.texastorque.torquelib.util;

/**
 * A static class of useful math utilities.
 *
 * @author Justus Languell
 */
public final class TorqueMathUtil {
    private TorqueMathUtil() {}

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
     * Main function to run tests on this class.
     */
    public static final void main(final String[] arguments) { System.out.println(round(123.4567, -2)); }
}