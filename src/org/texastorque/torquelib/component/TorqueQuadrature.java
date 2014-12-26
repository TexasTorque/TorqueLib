package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class TorqueQuadrature extends Encoder {

    private double averageRate;
    private double acceleration;
    private double previousTime;
    private double previousPosition;
    private double previousRate;

    public TorqueQuadrature(int aChannel, int bChannel, int indexChannel, boolean reverseDirection) {
        super(aChannel, bChannel, indexChannel, reverseDirection);
    }

    public TorqueQuadrature(int aChannel, int bChannel, boolean reverseDireciton, CounterBase.EncodingType encodingType) {
        super(aChannel, bChannel, reverseDireciton, encodingType);
    }
    
    public void calc() {
        double currentTime = Timer.getFPGATimestamp();
        double currentPosition = super.get();

        averageRate = (currentPosition - previousPosition) / (currentTime - previousTime);
        acceleration = (averageRate - previousRate) / (currentTime - previousTime);

        previousTime = currentTime;
        previousPosition = currentPosition;
        previousRate = averageRate;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
