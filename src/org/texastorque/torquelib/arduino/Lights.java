package org.texastorque.torquelib.arduino;

public class Lights {

    private Arduino sensor;
    private LightState prevState;

    public Lights(Arduino a) {
        sensor = a;
    }

    /**
     * Set light state.
     *
     * @param state New state.
     */
    public void set(LightState state) {
        if (state != prevState) {
            sensor.send(new byte[]{state.getData()});
            prevState = state;
        }
    }

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
