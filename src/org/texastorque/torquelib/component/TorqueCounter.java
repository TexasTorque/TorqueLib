package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.AnalogTrigger;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class TorqueCounter extends Counter {

    private double averageRate;
    private double acceleration;
    private double previousTime;
    private double previousPosition;
    private double previousRate;

    public TorqueCounter(int port) {
        super(kPwmChannels);
    }

    public TorqueCounter(DigitalInput source) {
        super(source);
    }

    public TorqueCounter(int upPort, int downPort, boolean reverse, CounterBase.EncodingType encodingype) {
        super(encodingype, new DigitalInput(upPort), new DigitalInput(downPort), reverse);

    }

    public TorqueCounter(AnalogTrigger trigger) {
        super(trigger);
    }

    public void calc() {
        double currentTime = Timer.getFPGATimestamp();

        averageRate = (super.get() - previousPosition) / (currentTime - previousTime);
        acceleration = (averageRate - previousRate) / (currentTime - previousTime);

        previousTime = currentTime;
        previousPosition = super.get();
        previousRate = averageRate;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
