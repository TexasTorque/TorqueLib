package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogTrigger;
import org.texastorque.torquelib.util.MovingAverageFilter;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalSource;

public class TorqueCounter extends TorqueEncoder {

    public static final CounterBase.EncodingType k1X = CounterBase.EncodingType.k1X;
    public static final CounterBase.EncodingType k2X = CounterBase.EncodingType.k2X;

    private MovingAverageFilter filter;

    public TorqueCounter(int port) {
        this(new DigitalInput(port));
    }

    public TorqueCounter(CounterBase.EncodingType encodingType, DigitalSource upSource, DigitalSource downSource, boolean reverse) {
        encoder = new Counter(encodingType, upSource, downSource, reverse);

        filter = new MovingAverageFilter(1);
        filter.reset();
    }
    
    public TorqueCounter(AnalogTrigger trigger)
    {
        encoder = new Counter(trigger);
        
        filter = new MovingAverageFilter(1);
        filter.reset();
    }
    
    public TorqueCounter(DigitalSource source)
    {
        encoder = new Counter(source);
        
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
