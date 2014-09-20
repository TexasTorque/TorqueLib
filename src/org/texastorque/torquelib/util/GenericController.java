package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;

public class GenericController extends Joystick {

    public static final boolean LOGITECH = true;
    public static final boolean XBOX = false;

    private boolean isLogitechController;

    private double deadband;

    public GenericController(int port, boolean isLogitech, double dband) {
        super(port);
        isLogitechController = isLogitech;
        deadband = Math.max(1, dband);
    }

    //scales inputs [deadband, 1] to [0, 1]
    private double scaleInput(double input) {
        if (Math.abs(input) > 0) {
            if (input > 0) {
                return (input - deadband) / (1 - deadband);
            } else {
                return (input + deadband) / (1 - deadband);
            }
        } else {
            return 0.0;
        }
    }

    public void setDeadband(double dband) {
        deadband = Math.max(1, dband);
    }

    public synchronized void setType(boolean isLog) {
        isLogitechController = isLog;
    }

    public synchronized boolean isLogitech() {
        return isLogitechController;
    }

    public synchronized double getLeftYAxis() {
        if (isLogitechController) {
            return scaleInput(getRawAxis(2));
        } else {
            return scaleInput(getRawAxis(2));
        }
    }

    public synchronized double getLeftXAxis() {
        if (isLogitechController) {
            return scaleInput(getRawAxis(1));
        } else {
            return scaleInput(getRawAxis(1));
        }
    }

    public synchronized double getRightYAxis() {
        if (isLogitechController) {
            return scaleInput(getRawAxis(4));
        } else {
            return scaleInput(getRawAxis(5));
        }
    }

    public synchronized double getRightXAxis() {
        if (isLogitechController) {
            return scaleInput(getRawAxis(3));
        } else {
            return scaleInput(getRawAxis(4));
        }
    }

    public synchronized boolean getLeftDPAD() {
        if (isLogitechController) {
            return (getRawAxis(5) > 0.0);
        } else {
            return (getRawAxis(6) > 0.0);
        }
    }

    public synchronized boolean getRightDPAD() {
        if (isLogitechController) {
            return (getRawAxis(5) < 0.0);
        } else {
            return (getRawAxis(6) < 0.0);
        }
    }

    public synchronized boolean getLeftStickClick() {
        if (isLogitechController) {
            return getRawButton(11);
        } else {
            return getRawButton(9);
        }
    }

    public synchronized boolean getRightStickClick() {
        if (isLogitechController) {
            return getRawButton(12);
        } else {
            return getRawButton(10);
        }
    }

    public synchronized boolean getTopLeftBumper() {
        if (isLogitechController) {
            return getRawButton(5);
        } else {
            return getRawButton(5);
        }
    }

    public synchronized boolean getTopRightBumper() {
        if (isLogitechController) {
            return getRawButton(6);
        } else {
            return getRawButton(6);
        }
    }

    public synchronized boolean getBottomLeftBumper() {
        if (isLogitechController) {
            return getRawButton(7);
        } else {
            return (getRawAxis(3) > 0.2);
        }
    }

    public synchronized boolean getBottomRightBumper() {
        if (isLogitechController) {
            return getRawButton(8);
        } else {
            return (getRawAxis(3) < -0.2);
        }
    }

    public synchronized boolean getLeftCenterButton() {
        if (isLogitechController) {
            return getRawButton(9);
        } else {
            return getRawButton(7);
        }
    }

    public synchronized boolean getRightCenterButton() {
        if (isLogitechController) {
            return getRawButton(10);
        } else {
            return getRawButton(8);
        }
    }

    public synchronized boolean getLeftActionButton() {
        if (isLogitechController) {
            return getRawButton(1);
        } else {
            return getRawButton(3);
        }
    }

    public synchronized boolean getTopActionButton() {
        if (isLogitechController) {
            return getRawButton(4);
        } else {
            return getRawButton(4);
        }
    }

    public synchronized boolean getRightActionButton() {
        if (isLogitechController) {
            return getRawButton(3);
        } else {
            return getRawButton(2);
        }
    }

    public synchronized boolean getBottomActionButton() {
        if (isLogitechController) {
            return getRawButton(2);
        } else {
            return getRawButton(1);
        }
    }
}
