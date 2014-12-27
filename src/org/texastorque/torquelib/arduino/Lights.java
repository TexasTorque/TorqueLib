package org.texastorque.torquelib.arduino;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class Lights {

    private I2C i2c;
    private LightState state;

    /**
     * Create a new set of Arduino lights.
     *
     * @param address The Arduino's device address. Pass the same address you used
     * in the arduino code.
     */
    public Lights(int address) {
        //Bithsift the address because robot uses 8 bit i2c addresses while the arduino
        //uses 7 bit addresses.
        i2c = new I2C(Port.kOnboard, address<<1);
        state = LightState.WHITE;
    }

    /**
     * Set light state.
     *
     * @param newState New state.
     */
    public void set(LightState newState) {
        if (newState != state) {
            byte[] ary = new byte[]{newState.getData()};
            i2c.transaction(ary, ary.length, null, 0);
            state = newState;
        }
    }

    /**
     * Get the current state of the Arduino lights.
     *
     * @return The current LightState.
     */
    public LightState getState() {
        return state;
    }

    /**
     * The state that the lights indicate.
     */
    public enum LightState {

        WHITE((byte) 0),
        /**
         * Normal state on red alliance.
         */
        NORMAL_RED((byte) 1),
        /**
         * Normal state on blue alliance.
         */
        NORMAL_BLUE((byte) 2);

        private byte data;

        LightState(byte data) {
            this.data = data;
        }

        public byte getData() {
            return data;
        }
    }
}
