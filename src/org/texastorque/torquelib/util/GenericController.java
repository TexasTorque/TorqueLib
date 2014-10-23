package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;

public class GenericController extends Joystick {

    public static final int TYPE_LOGITECH = 1;
    public static final int TYPE_XBOX = 2;

    public static final boolean LOGITECH = true;
    public static final boolean XBOX = false;

    private int[] acc;
    private int controllerType;
    private double deadband;

    public GenericController(int port, int type, double dband) {
        super(port);
        controllerType = type;
        deadband = Math.min(1, Math.abs(dband));

        switch (type) {
            case TYPE_LOGITECH:
                acc = new int[]{2, 1, 4, 3, 5, 5, 11, 12, 5, 6, 7, 8, 9, 10, 1, 4, 3, 2};
                break;
            case TYPE_XBOX:
                acc = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 3, 3, 7, 8, 3, 4, 2, 1};
                break;
            default:
                //default to logitech
                acc = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 3, 3, 7, 8, 3, 4, 2, 1};
                controllerType = TYPE_XBOX;
        }
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

    @Override
    public double getRawAxis(int port) {
        try {
            return super.getRawAxis(port);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void setDeadband(double dband) {
        deadband = Math.min(1, Math.abs(dband));
    }

    public synchronized void setType(int type) {
        controllerType = type;
        switch (type) {
            case TYPE_LOGITECH:
                acc = new int[]{2, 1, 4, 3, 5, 5, 11, 12, 5, 6, 7, 8, 9, 10, 1, 4, 3, 2};
                break;
            case TYPE_XBOX:
                acc = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 3, 3, 7, 8, 3, 4, 2, 1};
                break;
            default:
                //default to logitech
                acc = new int[]{2, 1, 5, 4, 6, 6, 9, 10, 5, 6, 3, 3, 7, 8, 3, 4, 2, 1};
                controllerType = TYPE_XBOX;
        }
    }

    public synchronized int getType() {
        return controllerType;
    }

    public synchronized double getLeftYAxis() {
        return scaleInput(getRawAxis(acc[0]));
    }

    public synchronized double getLeftXAxis() {
        return scaleInput(getRawAxis(acc[1]));
    }

    public synchronized double getRightYAxis() {
        return scaleInput(getRawAxis(acc[2]));
    }

    public synchronized double getRightXAxis() {
        return scaleInput(getRawAxis(acc[3]));
    }

    public synchronized boolean getLeftDPAD() {
        return (getRawAxis(acc[4]) > 0.0);
    }

    public synchronized boolean getRightDPAD() {
        return (getRawAxis(acc[5]) < 0.0);
    }

    public synchronized boolean getLeftStickClick() {
        return getRawButton(acc[6]);
    }

    public synchronized boolean getRightStickClick() {
        return getRawButton(acc[7]);
    }

    public synchronized boolean getRightBumper() {
        return getRawButton(acc[9]);
    }

    public synchronized boolean getLeftTrigger() {//10
        if (controllerType == TYPE_LOGITECH) {
            return getRawButton(7);
        } else if (controllerType == TYPE_XBOX) {
            return (getRawAxis(3) > 0.2);
        } else {
            return false;
        }
    }

    public synchronized boolean getRightTrigger() {//11
        if (controllerType == TYPE_LOGITECH) {
            return getRawButton(8);
        } else if (controllerType == TYPE_XBOX) {
            return (getRawAxis(3) < -0.2);
        } else {
            return false;
        }
    }

    public synchronized boolean getLeftCenterButton() {
        return getRawButton(acc[12]);
    }

    public synchronized boolean getRightCenterButton() {
        return getRawButton(acc[13]);
    }

    public synchronized boolean getXButton() {
        return getRawButton(acc[14]);
    }

    public synchronized boolean getYButton() {
        return getRawButton(acc[15]);
    }

    public synchronized boolean getBButton() {
        return getRawButton(acc[16]);
    }

    public synchronized boolean getAButton() {
        return getRawButton(acc[17]);
    }
}
