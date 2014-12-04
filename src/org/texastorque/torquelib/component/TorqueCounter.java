package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogTrigger;
import org.texastorque.torquelib.util.MovingAverageFilter;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

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

    public void calc() {
        double currentTime = Timer.getFPGATimestamp();
        currentPosition = encoder.get();
        
        secantRate = (currentPosition - previousPosition) / (currentTime - previousTime);
        instantRate = 1.0 / encoder.getPeriod();
        acceleration = (secantRate - previousRate) / (currentTime - previousTime);
        
        filter.setInput(secantRate);
        filter.run();

        previousTime = currentTime;
        previousPosition = currentPosition;
        previousRate = secantRate;
    }

    public void reset() {
        filter.reset();
        encoder.reset();
    }
}
