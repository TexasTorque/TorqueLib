package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogTrigger;
import org.texastorque.torquelib.util.MovingAverageFilter;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;

public class TorqueCounter extends TorqueEncoder {

    public static final CounterBase.EncodingType k1X = CounterBase.EncodingType.k1X;
    public static final CounterBase.EncodingType k2X = CounterBase.EncodingType.k2X;

    private MovingAverageFilter filter;

    public TorqueCounter(int port) {
        encoder = new Counter(new DigitalInput(port));

        filter = new MovingAverageFilter(1);
        filter.reset();
    }

    public TorqueCounter(int countPort, int directionPort, boolean reverse, CounterBase.EncodingType encodingype) {
        encoder = new Counter(encodingype, new DigitalInput(countPort), new DigitalInput(directionPort), reverse);

        filter = new MovingAverageFilter(1);
        filter.reset();
    }

    public TorqueCounter(AnalogTrigger trigger) {
        encoder = new Counter(trigger);

        filter = new MovingAverageFilter(1);
        filter.reset();
    }

    public void setFilterSize(int size) {
        filter = new MovingAverageFilter(size);
        filter.reset();
    }

    @Override
    public void calc() {
        double currentRate = 1.0 / encoder.getPeriod();
        filter.setInput(currentRate);
        filter.run();

        currentPosition = encoder.get();
        rate = filter.getAverage();
    }

    @Override
    public void reset() {
        filter.reset();
        encoder.reset();
    }
}
