package org.texastorque.torquelib.arduino;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class Arduino {

    private final I2C i2c;

    /**
     * Create a new Arudino board.
     *
     * @param address The Arduino's device address.
     */
    public Arduino(int address) {
        i2c = new I2C(Port.kOnboard, address);
    }

    /**
     * Send bytes to Arduino.
     *
     * @param data Byte[] of data.
     * @return True means that the operation finished uninterrupted.
     */
    public boolean send(byte[] data) {
        return i2c.transaction(data, data.length, null, 0);
    }

    /**
     * Get Arduino board data.
     *
     * @return Byte[] of data.
     */
    public byte[] get() {
        byte[] data = null;
        i2c.readOnly(data, 4);
        return data;
    }
}
