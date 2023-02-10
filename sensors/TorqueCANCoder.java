package org.texastorque.torquelib.sensors;

import com.ctre.phoenix.sensors.CANCoder;

public final class TorqueCANCoder extends CANCoder {
    private double lastTimestamp = 0;

    public TorqueCANCoder(int port) {
        super(port);
    }

    public final double getDeltaTimestamp() {
        final double current = getLastTimestamp();
        final double delta = current - lastTimestamp;
        lastTimestamp = current;
        return delta;
    }

    public final boolean isCANResponsive() {
        return getDeltaTimestamp() == 0;
    }

}
