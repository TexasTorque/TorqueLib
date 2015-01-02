package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Class for all WPILib motors with built in reversing.
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
     *
     * SpeedController is an interface implemented by Victor, Talon, Jaguar.
     * @see edu.wpi.first.wpilibj.SpeedController
     */
    public Motor(SpeedController sc, boolean rev) {
        controller = sc;
        reverse = rev;
    }

    /**
     * Set the speed of the motor.
     *
     * @param speed The speed to be set to the output.
     */
    public void set(double speed) {
        //Dont update if it did not change.
        if (speed != previousSpeed) {

            //Reverse if required.
            if (reverse) {
                speed *= -1;
            }
            controller.set(speed);

            previousSpeed = speed;
        }
    }
}
