package org.texastorque.torquelib.component;

import org.texastorque.torquelib.util.TorqueMathUtil;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/**
 * L-16R Linear Servo class.
 * 
 * How much of this is necessary?
 * Probably not much, but 4414 initially 
 * wrote much of this so there has to be
 * some point to it.
 * 
 * The thing I dont exactly understand is
 * the stuff about "must be calling 
 * {@link #updateCurPos() updateCurPos()} 
 * periodically".
 * 
 * @author Justus
 */
public class TorqueLinearServo extends Servo {
    private double speed, length, setPosition, currentPosition, lastTime;

    /**
     * Constructs a new TorqueLinearServo.
     * 
     * @param port The PWM port.
     * @param length Max length (mm).
     * @param speed Max speed (?).
     */
    public TorqueLinearServo(int port, int length, int speed) {
        super(port);
        this.length = length;
        this.speed = speed;
        lastTime = 0;

        // max, max deadband, center, min deadband, min
        setBounds(2., 1.8, 1.5, 1.2, 1.); // should be constant for all? 
    }

    /** 
     * Set the target position.
     * 
     * @param setpoint Target position (mm).
     */
    public void setPosition(double setpoint) {
        setSpeed((TorqueMathUtil.constrain(setpoint, 0, length) / length * 2) - 1);
    }

    /**
     * Update position estimation 
     */
    public void updateCurrentPosition() {
        double dt = Timer.getFPGATimestamp() - lastTime;

        if (currentPosition > setPosition + speed * dt)
            currentPosition -= speed * dt;
        else if (currentPosition < setPosition - speed * dt)
            currentPosition += speed * dt;
        else
            currentPosition = setPosition;
    }

    /**
     * Current position of the servo.
     * 
     * @return Servo Position (mm)
     */
    public double getPosition() {
        return currentPosition;
    }

    /**
     * Checks if the servo is at its target position.
     * 
     * @return True if servo is at target position.
     */
    public boolean isFinished() {
        return currentPosition == setPosition;
    }
}