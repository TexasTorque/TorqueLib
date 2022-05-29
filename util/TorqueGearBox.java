package org.texastorque.torquelib.util;

/**
 * Class to implement a gearbox.
 * 
 * @author Justus Languell
 */
public final class TorqueGearBox {
    private final double ratio;

     /**
     * Builds a gearbox based the amount of times the output turns on 1 turn of the input.
     * 
     * @param output The size of the output gear.
     * @param input The size of the input gear.
     */
    public TorqueGearBox(final double ratio) {
        this.ratio = ratio;
    }

    /**
     * Builds a gearbox based on the ratio between the output and input.
     * 
     * @param output The size of the output gear.
     * @param input The size of the input gear.
     */
    public TorqueGearBox(final double output, final double input) {
        ratio = output / input;
    }

    /**
     * Returns the gear ratio.
     * 
     * @return The gear ratio.
     */
    public final double getRatio() {
        return ratio;
    }

    /**
     * Calculates a setpoint of the mechanism to a motor setpoint.
     * 
     * @param input Mechanism setpoint.
     * 
     * @return The setpoint of the motor.
     */
    public final double mechanismToMotor(final double input) {
        return input * ratio;
    }

    /**
     * Calculates a setpoint of the motor to a mechanism setpoint.
     * 
     * @param input Motor setpoint.
     * 
     * @return The setpoint of the mechanism.
     */
    public final double motorToMechanism(final double input) {
        return input / ratio;
    }
}
