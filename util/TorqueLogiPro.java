package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * @author TexasTorque 2021
 */
public class TorqueLogiPro extends Joystick {
    protected final int port;
    private double deadband;
    private static final double DEADBAND_DEF = .01;

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

    public boolean getTrigger() { 
        return getRawButton(0); 
    }

    public boolean getThumb() { 
        return getRawButton(1); 
    }

    public PovState getPovState() { 
        return PovState.NORTH; // Fix this
    }

    public double getRoll() { 
        return deadbanded(getX()); // getRawAxis(1)
    }

    public double getPitch() { 
        return deadbanded(-getY()); // getRawAxis(2)
    }

    public double getYaw() { 
        return deadbanded(getZ()); // getRawAxis(3)
    }

    public double getThrottle() { 
        return deadbanded(getRawAxis(4)); 
    }

    public static enum PovState {
        NORTH("NORTH"), NORTH_EAST("NORTH EAST"), EAST("EAST"), SOUTH_EAST("SOUTH EAST"), 
        SOUTH("SOUTH"), SOUTH_WEST("SOUTH WEST"), WEST("WEST"), NORTH_WEST("SOUTH WEST"); 

        private String value;

        PovState(String value) {
            this.value = value;
        }

        String asString() { 
            return value; 
        }
    }
}
