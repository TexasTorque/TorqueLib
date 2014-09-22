package org.texastorque.torquelib.component;

import org.texastorque.torquelib.util.MovingAverageFilter;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalSource;

public class TorqueCounter {

    private Counter counter;

    private MovingAverageFilter filter;

    public TorqueCounter(int port) {
        counter = new Counter(port);

        filter = new MovingAverageFilter(1);
    }

    public TorqueCounter(CounterBase.EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse) {
        counter = new Counter(encodingType, upSource, downSource, reverse);

        filter = new MovingAverageFilter(1);
        filter.reset();
    }

    public void setFilterSize(int size) {
        filter = new MovingAverageFilter(size);
        filter.reset();
    }

    public void calc() {
        double rate = 1.0 / counter.getPeriod();
        filter.setInput(rate);
        filter.run();
    }

    public void start() {
        filter.reset();
    }

    public void reset() {
        filter.reset();
        counter.reset();
    }

    public int get() {
        return counter.get();
    }

    public double getRate() {
        return filter.getAverage();
    }
}
