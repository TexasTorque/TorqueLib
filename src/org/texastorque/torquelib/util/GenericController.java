package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A class that reads input from either a Logitech or Xbox controller.
 *
 * @author TexasTorque
 */
public final class GenericController extends Joystick {

    public static final int TYPE_LOGITECH = 1;
    public static final int TYPE_XBOX = 2;

    private int[] controllerMap;
    private int controllerType;
    private double deadband;

    /**
     * Create a new controller.
     *
     * @param port Which Driver Station port the controller is in (use constant).
     * @param type Type of controller.
     * @param dband The size of the deadband.
     */
    public GenericController(int port, int type, double dband) {
        super(port);
        controllerType = type;
        deadband = Math.min(1, Math.abs(dband));

        setType(controllerType);
    }

    //scales inputs [deadband, 1] to [0, 1]
    private double scaleInput(double input) {
        if (Math.abs(input) > deadband) {
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
        deadband = Math.min(1, Math.abs(dband));
    }

    /**
     * Change controller type. Will default to Xbox if an incorrect type is
     * given.
     *
     * @param type New controller type.
     */
    public synchronized void setType(int type) {
        controllerType = type;
        switch (type) {
            case TYPE_LOGITECH:
                controllerMap = new int[]{2, 1, 4, 3, 5, 5, 11, 12, 5, 6, 7, 8, 9, 10, 1, 4, 3, 2};
                break;
            case TYPE_XBOX:
                controllerMap = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 2, 3, 7, 8, 3, 4, 2, 1};
                break;
            default:
                //default to xbox
                controllerMap = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 3, 3, 7, 8, 3, 4, 2, 1};
                controllerType = TYPE_XBOX;
        }
    }

    public synchronized int getType() {
        return controllerType;
    }

    public synchronized double getLeftYAxis() {
        return scaleInput(getRawAxis(controllerMap[0]));
    }

    public synchronized double getLeftXAxis() {
        return scaleInput(getRawAxis(controllerMap[1]));
    }

    public synchronized double getRightYAxis() {
        return scaleInput(getRawAxis(controllerMap[2]));
    }

    public synchronized double getRightXAxis() {
        return scaleInput(getRawAxis(controllerMap[3]));
    }

    public synchronized boolean getLeftDPAD() {
        return (getRawAxis(controllerMap[4]) > 0.0);
    }

    public synchronized boolean getRightDPAD() {
        return (getRawAxis(controllerMap[5]) < 0.0);
    }

    public synchronized boolean getLeftStickClick() {
        return getRawButton(controllerMap[6]);
    }

    public synchronized boolean getRightStickClick() {
        return getRawButton(controllerMap[7]);
    }

    public synchronized boolean getLeftBumper() {
        return getRawButton(controllerMap[8]);
    }

    public synchronized boolean getRightBumper() {
        return getRawButton(controllerMap[9]);
    }

    public synchronized boolean getLeftTrigger() {
        if (controllerType == TYPE_LOGITECH) {
            return getRawButton(controllerMap[10]);
        } else if (controllerType == TYPE_XBOX) {
            return (getRawAxis(controllerMap[10]) > 0.2);
        } else {
            return false;
        }
    }

    public synchronized boolean getRightTrigger() {
        if (controllerType == TYPE_LOGITECH) {
            return getRawButton(controllerMap[11]);
        } else if (controllerType == TYPE_XBOX) {
            return (getRawAxis(controllerMap[11]) > 0.2);
        } else {
            return false;
        }
    }

    public synchronized boolean getLeftCenterButton() {
        return getRawButton(controllerMap[12]);
    }

    public synchronized boolean getRightCenterButton() {
        return getRawButton(controllerMap[13]);
    }

    public synchronized boolean getXButton() {
        return getRawButton(controllerMap[14]);
    }

    public synchronized boolean getYButton() {
        return getRawButton(controllerMap[15]);
    }

    public synchronized boolean getBButton() {
        return getRawButton(controllerMap[16]);
    }

    public synchronized boolean getAButton() {
        return getRawButton(controllerMap[17]);
    }

    public synchronized void setLeftRumble(boolean on) {
        setRumble(Joystick.RumbleType.kLeftRumble, on ? 1 : 0);
    }

    public synchronized void setrightRumble(boolean on) {
        setRumble(Joystick.RumbleType.kRightRumble, on ? 1 : 0);
    }
}
