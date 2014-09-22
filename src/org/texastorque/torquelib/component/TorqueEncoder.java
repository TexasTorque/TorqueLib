package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;

public class TorqueEncoder {

    private Encoder encoder;

    private double previousTime;
    private double prevoiusPosition;
    private double previousRate;
    private int currentPosition;

    private double rate;
    private double acceleration;

    public TorqueEncoder(int aChannel, int bChannel, boolean reverseDirection) {
        encoder = new Encoder(aChannel, bChannel, reverseDirection);
    }

    public TorqueEncoder(int aChannel, int bChannel, boolean reverseDireciton, CounterBase.EncodingType encodingType) {
        encoder = new Encoder(aChannel, bChannel, reverseDireciton, encodingType);
    }

    public void reset() {
        encoder.reset();
    }

    public void calc() {
        double currentTime = Timer.getFPGATimestamp();
        currentPosition = encoder.get();

        rate = (currentPosition - prevoiusPosition) / (currentTime - previousTime);
        acceleration = (rate - previousRate) / (currentTime - previousTime);

        previousTime = currentTime;
        prevoiusPosition = currentPosition;
        previousRate = rate;
    }

    public int get() {
        return currentPosition;
    }

    public double getRate() {
        return rate;
    }

    public double getAcceleration() {
        return acceleration;
    }
}
