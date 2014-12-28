package org.texastorque.torquelib.util;

import java.util.ArrayList;

/**
 * A list of values from which an average can be pulled. The list has a
 * user-defined maximum size so that the list overrides itself after a certain
 * number of values added.
 */
public class DynamicAverage {

    private ArrayList<Double> values;
    private int position;
    private double average;
    private int maxSize;

    /**
     * Create an list of value from which an average can be determined.
     *
     * @param maxSize_ The initial maximum size of the list.
     */
    public DynamicAverage(int maxSize_) {
        values = new ArrayList<>();
        if (maxSize_ < 2) {
            maxSize = 10;
        } else {
            maxSize = maxSize_;
        }
    }

    /**
     * Reset the list.
     */
    public synchronized void reset() {
        values = new ArrayList<>();
        position = 0;
        average = 0.0;
    }

    /**
     * Add a value to the list.
     *
     * @param value The next value.
     */
    public synchronized void add(Double value) {
        if (position >= maxSize) {
            position = 0;
        }
        values.add(position, value);
    }

    /**
     * Get the current average of the list.
     *
     * @return The average.
     */
    public synchronized double getAverage() {
        for (Double n : values) {
            average += n;
        }
        average /= values.size();
        return average;
    }

    /**
     * Set the maximum size of the list.
     *
     * @param maxSize_ The maximum size.
     */
    public synchronized void setMaxSize(int maxSize_) {
        maxSize = maxSize_;
    }

    /**
     * Get the maximum size of the list.
     *
     * @return The maximum size.
     */
    public synchronized int getMaxSize() {
        return maxSize;
    }
}
