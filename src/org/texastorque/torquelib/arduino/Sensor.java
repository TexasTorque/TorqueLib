package org.texastorque.torquelib.arduino;

public class Sensor {

    private Arduino arduino;

    /**
     * Create a new Arduino sensor.
     *
     * @param a The Arduino board.
     */
    public Sensor(Arduino a) {
        arduino = a;
    }

    /**
     * Returns whether or not the sensor is pressed.
     *
     * Nomsaucer is this right?
     *
     * @param index
     * @return True means that the sensor is pressed.
     */
    public boolean get(int index) {
        return arduino.get()[index] == 1;
    }
}
