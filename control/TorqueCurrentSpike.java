package org.texastorque.torquelib.control;

/**
 * A class that can be used to detect current spikes.
 * 
 * @author Omar Afzal
 */
public final class TorqueCurrentSpike {

    private CurrentSpikes status = CurrentSpikes.NONE;
    private final TorqueClick currentClick = new TorqueClick();

    public static enum CurrentSpikes {
        NONE, STARTUP, SPIKE;

        public CurrentSpikes next() {
            return values()[Math.min(ordinal() + 1, values().length - 1)];
        }

    }

    private final double currentLimit;

    public TorqueCurrentSpike(final double currentLimit) {
        this.currentLimit = currentLimit;
    }

    public boolean calculate(final double currentCurrent) {
        if (currentClick.calculate(currentCurrent >= currentLimit)) 
            status = status.next();

        return status == CurrentSpikes.SPIKE;
    }

    public void reset() {
        status = CurrentSpikes.NONE;
    }

    public CurrentSpikes getStatus() {
        return status;
    }

}
