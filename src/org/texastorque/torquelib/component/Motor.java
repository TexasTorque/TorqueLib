package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.SpeedController;

public class Motor {

    private SpeedController controller;
    private boolean reverse;

    private double previousSpeed;

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

    static float LimitSpeed(float speed, float limit) {
        if (Math.abs(speed) <= limit) {
            return speed;
        } else if (speed < 0) {
            return -limit;
        }
        return limit;
    }
}
