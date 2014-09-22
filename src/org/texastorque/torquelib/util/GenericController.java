package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GenericController extends Joystick {

    private boolean isLogitechController;

    public GenericController(int port) {
        super(port);
        isLogitechController = true;
    }

    public GenericController(int port, boolean isLogitech) {
        super(port);
        isLogitechController = isLogitech;
    }

    public synchronized void setAsLogitech() {
        isLogitechController = true;
    }

    public synchronized void setAsXBox() {
        isLogitechController = false;
    }

    public synchronized boolean isLogitech() {
        return isLogitechController;
    }

    public synchronized double getLeftYAxis() {
        if (isLogitechController) {
            return getRawAxis(2);
        } else {
            return getRawAxis(2);
        }
    }

    public synchronized double getLeftXAxis() {
        if (isLogitechController) {
            return getRawAxis(1);
        } else {
            return getRawAxis(1);
        }
    }

    public synchronized double getRightYAxis() {
        if (isLogitechController) {
            return getRawAxis(4);
        } else {
            return getRawAxis(5);
        }
    }

    public synchronized double getRightXAxis() {
        if (isLogitechController) {
            return getRawAxis(3);
        } else {
            return getRawAxis(4);
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
