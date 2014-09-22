package org.texastorque.torquelib.arduino;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class Arduino {

    private final I2C i2c;

    public Arduino() {
        i2c = new I2C(Port.kOnboard, 1);
    }

    public boolean send(byte[] data) {
        return i2c.transaction(data, data.length, null, 0);
    }

    public byte[] get() {
        byte[] data = null;
        i2c.readOnly(data, 4);
        return data;
    }
}
