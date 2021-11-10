package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class TorqueLogiPro extends Joystick {
    private final int port;
    private double deadband;
    private static final double DEADBAND_DEF = .01;
    public JoystickButton[] buttons;

    public TorqueLogiPro(int port) {
        super(port);
        this.port = port;
        deadband = DEADBAND_DEF;
    }

    public TorqueLogiPro(int port, double deadband) {
        super(port);
        this.port = port;
        this.deadband = deadband;
    }

    private double deadbanded(double value) {
        return (value < deadband && value > -deadband) ? 0 : value;
    }

    public boolean getButtonByIndex(int index) {
        return getRawButton(index);
    }

    public boolean getTrigger() { return getRawButton(0); }
    public boolean getThumb() { return getRawButton(1); }

    public double getRoll() { return deadbanded(getX()); } 
    public double getPitch() { return deadbanded(-getY()); }
    public double getYaw() { return deadbanded(getZ()); }
}
