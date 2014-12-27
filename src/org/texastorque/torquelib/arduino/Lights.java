package org.texastorque.torquelib.arduino;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;

public class Lights {

    private I2C i2c;
    private LightState state;

    /**
     * Create a new set of Arduino lights.
     *
     * @param address The Arduino's device address.
     */
    public Lights(int address) {
        i2c = new I2C(Port.kOnboard, address);
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

        /**
         * Normal state on red alliance.
         */
        NORMAL_RED((byte) 0),
        /**
         * Normal state on blue alliance.
         */
        NORMAL_BLUE((byte) 1);

        private byte data;

        LightState(byte data) {
            this.data = data;
        }

        public byte getData() {
            return data;
        }
    }
}
