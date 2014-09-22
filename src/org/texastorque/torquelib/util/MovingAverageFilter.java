package org.texastorque.torquelib.util;

// Taken from team 341's 2012 robot code.
public class MovingAverageFilter {

    private double[] vals;
    private int ptr = 0;
    private double average = 0.0;

    public MovingAverageFilter(int nSamples) {
        vals = new double[nSamples];
    }

    public synchronized void reset() {
        for (int i = 0; i < vals.length; i++) {
            vals[i] = 0.0;
        }
        ptr = 0;
        average = 0.0;
    }

    public synchronized void setInput(double val) {
        vals[ptr] = val;
        ptr++;

        if (ptr >= vals.length) {
            ptr = 0;
        }
    }

    public synchronized double getAverage() {
        return average;
    }

    public synchronized double run() {
        average = 0.0;
        for (int i = 0; i < vals.length; i++) {
            average += vals[i];
        }
        average /= (double) vals.length;
        return average;
    }

}
