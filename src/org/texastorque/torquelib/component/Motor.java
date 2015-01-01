package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Class for all WPILib motors.
 *
 * @author TexasTorque
 */
public class Motor {

    private SpeedController controller;
    private boolean reverse;

    private double previousSpeed;

    /**
     * Create a new motor.
     *
     * @param sc The SpeedController object.
     * @param rev Whether or not the motor is reversed.
     * @see edu.wpi.first.wpilibj.SpeedController
     */
    public Motor(SpeedController sc, boolean rev) {
        controller = sc;
        reverse = rev;
    }

    public void Set(double speed) {
        if (speed != previousSpeed) {
            if (reverse) {
                speed *= -1;
            }
            controller.set(speed);

            previousSpeed = speed;
        }
    }
}
