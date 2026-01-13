package org.texastorque.torquelib.sensors;

import com.ctre.phoenix6.hardware.CANcoder;
/**
 * A CANCoder that can be used to check if it is CAN responsive.
 * 
 * @author Omar Afzal
 */
@Deprecated
public final class TorqueCANCoder extends CANcoder {
    private double lastTimestamp = 0;

    public TorqueCANCoder(int port) {
        super(port);
    }

    public final double getDeltaTimestamp() {
        final double current = getDeltaTimestamp();
        final double delta = current - lastTimestamp;
        lastTimestamp = current;
        return delta;
    }

    public final boolean isCANResponsive() {
        return getDeltaTimestamp() != 0;
    }
}
