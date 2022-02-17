package org.texastorque.torquelib.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Rolling Median
 * 
 * o(nlogn)
 */
public class RollingMedian {
    private final int window;
    private final Queue<Double> med;

    /**
     * Constructs a new rolling median class, specifying the window size.
     * 
     * @param window The window size for each section (twice is the amount
     *               stored).
     */
    public RollingMedian(int window) {
        this.window = window;
        med = new LinkedList<>();
    }

    /**
     * Adds and performs calculation
     * 
     * @param value The value to add.
     * @return The median value.
     */
    public double calculate(double value) {
        if (med.size() >= window)
            med.poll();
        med.add(value);

        Double[] vals = med.toArray(new Double[0]);
        Arrays.sort(vals);
        if (vals.length % 2 == 0) {
            return (vals[vals.length / 2] + vals[vals.length / 2 - 1]) / 2.;
        } else {
            return vals[vals.length / 2];
        }
    }

    public static void main(String[] args) {
        RollingMedian rm = new RollingMedian(3);
        System.out.println(rm.calculate(7)); // 7
        System.out.println(rm.calculate(8)); // 7.5
        System.out.println(rm.calculate(9)); // 8
        System.out.println(rm.calculate(555)); // 9
        System.out.println(rm.calculate(3)); // 9
        System.out.println(rm.calculate(15)); // 15
        System.out.println(rm.calculate(17)); // 15
        System.out.println(rm.calculate(19)); // 17
        System.out.println(rm.calculate(21)); // 19
        System.out.println(rm.calculate(23)); // 21
    }
}