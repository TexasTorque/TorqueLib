package org.texastorque.torquelib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Rolling Median
 * 
 * Update on efficiency: I think this is O(n log n) 
 * as it utalizes a sort. I belive there are ways to
 * do it faster, but I don't know how to implement
 * them with the window. I think I have an idea of
 * how to do it with pairs so I am going to try that.
 *  
 * @author Justus
 */
class RollingMedian<T> {
    private final int window;
    private final List<T> list;
    private final Comparator<T> comparator;

    /**
     * An ready to use compare class for integers.
     * 
     */
    public static class IntegerCompare implements Comparator<Integer> {
        @Override
        public int compare(Integer a, Integer b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }
    }

      /**
     * An ready to use compare class for doubles.
     * 
     */
    public static class DoubleCompare implements Comparator<Double> {
        @Override
        public int compare(Double a, Double b) {
            return a < b ? -1 : a > b ? 1 : 0;
        }
    }    

    /**
     * Constructs a new rolling median class, specifying the window size,
     * and the comparator to use. 
     * 
     * @param window The window size.
     * @param comparator The comparator to use.
     */
    public RollingMedian(int window, Comparator<T> comparator) {
        this.window = window;
        this.comparator = comparator;
        this.list = new ArrayList<>(window);
    }

    /**
     * Adds and performs calculation 
     * 
     * @param value The value to add.
     * @return The median value.
     */
    public T calculate(T value) {
        if (list.size() == window) 
            list.remove(0);
       
        list.add(value);

        ArrayList<T> copy = new ArrayList<>(list);
        Collections.sort(copy, comparator);

        if (list.size() % 2 == 0)
            return list.get(list.size() / 2 - 1);
        else
            return list.get(list.size() / 2);
    }

    /**
     * Gets the list of values.
     * 
     * @return The list of values.
     */
    public List<T> getList() {
        return list;
    }
}