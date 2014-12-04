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

    public TorqueCounter(int port) {
        encoder = new Counter(new DigitalInput(port));
    }

    public TorqueCounter(int countPort, int directionPort, boolean reverse, CounterBase.EncodingType encodingype) {
        encoder = new Counter(encodingype, new DigitalInput(countPort), new DigitalInput(directionPort), reverse);
    }

    public TorqueCounter(AnalogTrigger trigger) {
        encoder = new Counter(trigger);
    }

    public void calc() {
        double currentTime = Timer.getFPGATimestamp();
        currentPosition = encoder.get();
        
        secantRate = (currentPosition - previousPosition) / (currentTime - previousTime);
        instantRate = 1.0 / encoder.getPeriod();
        acceleration = (secantRate - previousRate) / (currentTime - previousTime);
        
        previousTime = currentTime;
        previousPosition = currentPosition;
        previousRate = secantRate;
    }
}
