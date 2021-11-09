package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class TorqueLogiPro extends Joystick {
    private final int port;
    private double deadband;
    private static final int NUM_BUTTONS = 12;
    private static final double DEADBAND_DEF = .01;
    public JoystickButton[] buttons;

    public TorqueLogiPro(int port) {
        super(port);
        this.port = port;
        buttons = new JoystickButton[NUM_BUTTONS+1];
		for (int i = 1; i < NUM_BUTTONS+1; i++)
			buttons[i] = new JoystickButton(this, i);
        deadband = DEADBAND_DEF;
    }

    public TorqueLogiPro(int port, double deadband) {
        super(port);
        this.port = port;
        buttons = new JoystickButton[NUM_BUTTONS+1];
		for (int i = 1; i < NUM_BUTTONS+1; i++)
			buttons[i] = new JoystickButton(this, i);
        this.deadband = deadband;
    }

    private double deadbanded(double value) {
        return (value < deadband && value > -deadband) ? 0 : value;
    }

    public double getRoll() { return deadbanded(getX()); } 
    public double getPitch() { return deadbanded(-getY()); }
    public double getYaw() { return deadbanded(getZ()); }
}
