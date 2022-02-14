package org.texastorque.torquelib.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Rolling Median
 * 
 * o(n)
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
        med = new PriorityQueue<>(window);
        // a = new LinkedList<Double>();
        // exp = new LinkedList<Double>();
    }

    /**
     * Adds and performs calculation
     * 
     * @param value The value to add.
     * @return The median value.
     */
    public double calculate(double value) {
        if (med.size() > window)
            med.poll();
        med.add(value);

        Double[] vals = med.toArray(new Double[0]);
        Arrays.sort(vals);
        if (vals.length % 2 == 0) {
            return (vals[vals.length / 2] + vals[vals.length / 2 - 1]) / 2.;
        } else {
            return vals[vals.length / 2];
        }

        // // Add to removal queue
        // exp.add(value);

        // boolean removing = false;
        // double removeValue = 0;
        // if (a.size() >= window) {
        // removeValue = exp.poll();
        // removing = true;
        // }

        // // insert new value into list
        // // O(<=m)
        // ListIterator<Double> itr = a.listIterator();
        // int size = a.size();
        // if (!removing)
        // size++;
        // boolean inserted = false;
        // double median = 0;
        // int i = 0;
        // if (!itr.hasNext())
        // median = value;
        // while (itr.hasNext()) {
        // double next = itr.next();

        // if (size % 2 == 1 && size / 2 == i)
        // median = next;
        // else if (size % 2 == 0 && size / 2 == i || size / 2 - 1 == i)
        // median += next / 2;

        // if (removing && next == removeValue) {
        // itr.remove();
        // removing = false;
        // continue;
        // } else if (next > value) {
        // itr.previous();
        // itr.add(value);
        // }

        // i++;
        // }
        // if (!inserted) {
        // itr.add(value);
        // if (a.size() == 2)
        // median += value / 2;
        // }

        // return median;
    }

    public static void main(String[] args) {
        RollingMedian rm = new RollingMedian(4);
        System.out.println(rm.calculate(7));
        System.out.println(rm.calculate(8));
        System.out.println(rm.calculate(9));
        System.out.println(rm.calculate(15));
        System.out.println(rm.calculate(17));
        System.out.println(rm.calculate(19));
        System.out.println(rm.calculate(21));
        System.out.println(rm.calculate(23));
    }
}