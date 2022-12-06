/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.sensors;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.texastorque.torquelib.util.TorqueMath;

/**
 * A class representation of a controller, used for gathering
 * driver input.
 *
 * @apiNote Functional replacement for GenericController
 * ({@link org.texastorque.torquelib.legacy.GenericController})
 *
 * @author Justus Languell
 */
public final class TorqueController {
    private static final double DEFAULT_DEADBAND = 0.1, TRIGGER_DOWN_POSITION = 0.2;

    public static enum ControllerPort {
        DRIVER,
        OPERATOR;
    }

    private final double deadband;
    private final Joystick stick;

    /**
     * Create a new TorqueController.
     *
     * @param port The controller port.
     */
    public TorqueController(final int port) { this(port, DEFAULT_DEADBAND); }

    /**
     * Create a new TorqueController.
     *
     * @param port The controller port.
     * @param deadband The deadband.
     */
    public TorqueController(final int port, final double deadband) {
        this.stick = new Joystick(port);
        this.deadband = TorqueMath.constrain(deadband, 0, .99);
    }

    /**
     * Create a new TorqueController.
     *
     * @param port The controller port.
     */
    public TorqueController(final ControllerPort port) { this(port.ordinal()); }

    /**
     * Create a new TorqueController.
     *
     * @param port The controller port.
     * @param deadband The deadband.
     */
    public TorqueController(final ControllerPort port, final double deadband) { this(port.ordinal(), deadband); }

    /**
     * @param raw The raw joystick input value.
     *
     * @return The scaled input value.
     */
    private final double scale(final double raw) {
        return TorqueMath.scaledDeadband(raw, deadband);
    }

    // * Low level Joystick calls

    private final double axis(final int channel) { return stick.getRawAxis(channel); }

    private final boolean down(final int channel) { return stick.getRawButton(channel); }

    private final boolean pressed(final int channel) { return stick.getRawButtonPressed(channel); }

    private final boolean released(final int channel) { return stick.getRawButtonReleased(channel); }

    // * Axis interface functions

    /**
     * Get the value of the left Y axis.
     *
     * @return The value of the left Y axis.
     */
    public final double getLeftYAxis() { return scale(axis(1)); }

    /**
     * Get the value of the left X axis.
     *
     * @return The value of the left X axis.
     */
    public final double getLeftXAxis() { return scale(axis(0)); }

    /**
     * Get the value of the left Y axis.
     *
     * @return The value of the left Y axis.
     */
    public final double getRightYAxis() { return scale(axis(5)); }

    /**
     * Get the value of the left X axis.
     *
     * @return The value of the left X axis.
     */
    public final double getRightXAxis() { return scale(axis(4)); }

    // * Trigger interface functions

    /**
     * Get the value of the left trigger axis.
     *
     * @return The value of the left trigger axis.
     *
     * @apiNote this will be renamed to "getLeftTrigger".
     */
    public final double getLeftTriggerAxis() { return axis(2); }

    /**
     * Get the value of the right trigger axis.
     *
     * @return The value of the right trigger axis.
     *
     * @apiNote this will be renamed to "getRightTrigger".
     */
    public final double getRightTriggerAxis() { return axis(3); }

    /**
     * Check if the left trigger is currently down.
     *
     * @return Is the left trigger currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isLeftTriggerDown" method instead
     */
    @Deprecated
    public final boolean getLeftTrigger() {
        return isLeftTriggerDown();
    }

    /**
     * Check if the right trigger is currently down.
     *
     * @return Is the right trigger currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isRightTriggerDown" method instead
     */
    @Deprecated
    public final boolean getRightTrigger() {
        return isRightTriggerDown();
    }

    /**
     * Check if the left trigger is currently down.
     *
     * @return Is the left trigger currently down?
     */
    public final boolean isLeftTriggerDown() { return getLeftTriggerAxis() >= TRIGGER_DOWN_POSITION; }

    /**
     * Check if the right trigger is currently down.
     *
     * @return Is the right trigger currently down?
     */
    public final boolean isRightTriggerDown() { return getRightTriggerAxis() >= TRIGGER_DOWN_POSITION; }

    // * DEPRECATED Button interface functions

    /**
     * Check if the left stick is currently down.
     *
     * @return Is the left stick currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isLeftStickClickDown" method instead
     */
    @Deprecated
    public final boolean getLeftStickClick() {
        return down(9);
    }

    /**
     * Check if the right stick is currently down.
     *
     * @return Is the right stick currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isRightStickClickDown" method instead
     */
    @Deprecated
    public final boolean getRightStickClick() {
        return down(10);
    }

    /**
     * Check if the left bumper is currently down.
     *
     * @return Is the left bumper currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isLeftBumperDown" method instead
     */
    @Deprecated
    public final boolean getLeftBumper() {
        return down(5);
    }

    /**
     * Check if the right bumper is currently down.
     *
     * @return Is the right bumper currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isRightBumperDown" method instead
     */
    @Deprecated
    public final boolean getRightBumper() {
        return down(6);
    }

    /**
     * Check if the left center button is currently down.
     *
     * @return Is the left center button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isLeftCenterButtonDown" method instead
     */
    @Deprecated
    public final boolean getLeftCenterButton() {
        return down(7);
    }

    /**
     * Check if the right center button is currently down.
     *
     * @return Is the right center button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isRightCenterButtonDown" method instead
     */
    @Deprecated
    public final boolean getRightCenterButton() {
        return down(8);
    }

    /**
     * Check if the X button is currently down.
     *
     * @return Is the X button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isXButtonDown" method instead
     */
    @Deprecated
    public final boolean getXButton() {
        return down(3);
    }

    /**
     * Check if the Y button is currently down.
     *
     * @return Is the Y button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isYButtonDown" method instead
     */
    @Deprecated
    public final boolean getYButton() {
        return down(4);
    }

    /**
     * Check if the B button is currently down.
     *
     * @return Is the B button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isBButtonDown" method instead
     */
    @Deprecated
    public final boolean getBButton() {
        return down(2);
    }

    /**
     * Check if the A button is currently down.
     *
     * @return Is the A button currently down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isAButtonDown" method instead
     */
    @Deprecated
    public final boolean getAButton() {
        return down(1);
    }

    // * Button down interface functions

    /**
     * Check if the left stick  is currently down.
     *
     * @return Is the left stick  currently down?
     */
    public final boolean isLeftStickClickDown() { return down(9); }

    /**
     * Check if the right stick  is currently down.
     *
     * @return Is the right stick  currently down?
     */
    public final boolean isRightStickClickDown() { return down(10); }

    /**
     * Check if the left bumper is currently down.
     *
     * @return Is the left bumper currently down?
     */
    public final boolean isLeftBumperDown() { return down(5); }

    /**
     * Check if the right bumper is currently down.
     *
     * @return Is the right bumper currently down?
     */
    public final boolean isRightBumperDown() { return down(6); }

    /**
     * Check if the left center button is currently down.
     *
     * @return Is the left center button currently down?
     */
    public final boolean isLeftCenterButtonDown() { return down(7); }

    /**
     * Check if the right center button is currently down.
     *
     * @return Is the right center button currently down?
     */
    public final boolean isRightCenterButtonDown() { return down(8); }

    /**
     * Check if the X button is currently down.
     *
     * @return Is the X button currently down?
     */
    public final boolean isXButtonDown() { return down(3); }

    /**
     * Check if the Y button is currently down.
     *
     * @return Is the Y button currently down?
     */
    public final boolean isYButtonDown() { return down(4); }

    /**
     * Check if the B button is currently down.
     *
     * @return Is the B button currently down?
     */
    public final boolean isBButtonDown() { return down(2); }

    /**
     * Check if the A button is currently down.
     *
     * @return Is the A button currently down?
     */
    public final boolean isAButtonDown() { return down(1); }

    // * Button pressed interface functions

    /**
     * Check if the left stick  is being pressed.
     *
     * @return Is the left stick  being pressed?
     */
    public final boolean isLeftStickClickPressed() { return pressed(9); }

    /**
     * Check if the right stick  is being pressed.
     *
     * @return Is the right stick  being pressed?
     */
    public final boolean isRightStickClickPressed() { return pressed(10); }

    /**
     * Check if the left bumper is being pressed.
     *
     * @return Is the left bumper being pressed?
     */
    public final boolean isLeftBumperPressed() { return pressed(5); }

    /**
     * Check if the right bumper is being pressed.
     *
     * @return Is the right bumper being pressed?
     */
    public final boolean isRightBumperPressed() { return pressed(6); }

    /**
     * Check if the left center button is being pressed.
     *
     * @return Is the left center button being pressed?
     */
    public final boolean isLeftCenterButtonPressed() { return pressed(7); }

    /**
     * Check if the right center button is being pressed.
     *
     * @return Is the right center button being pressed?
     */
    public final boolean isRightCenterButtonPressed() { return pressed(8); }

    /**
     * Check if the X button is being pressed.
     *
     * @return Is the X button being pressed?
     */
    public final boolean isXButtonPressed() { return pressed(3); }

    /**
     * Check if the Y button is being pressed.
     *
     * @return Is the Y button being pressed?
     */
    public final boolean isYButtonPressed() { return pressed(4); }

    /**
     * Check if the B button is being pressed.
     *
     * @return Is the B button being pressed?
     */
    public final boolean isBButtonPressed() { return pressed(2); }

    /**
     * Check if the A button is being pressed.
     *
     * @return Is the A button being pressed?
     */
    public final boolean isAButtonPressed() { return pressed(1); }

    // * Button released interface functions

    /**
     * Check if the left stick  is being released.
     *
     * @return Is the left stick  being released?
     */
    public final boolean isLeftStickClickReleased() { return released(9); }

    /**
     * Check if the right stick  is being released.
     *
     * @return Is the right stick  being released?
     */
    public final boolean isRightStickClickReleased() { return released(10); }

    /**
     * Check if the left bumper is being released.
     *
     * @return Is the left bumper being released?
     */
    public final boolean isLeftBumperReleased() { return released(5); }

    /**
     * Check if the right bumper is being released.
     *
     * @return Is the right bumper being released?
     */
    public final boolean isRightBumperReleased() { return released(6); }

    /**
     * Check if the left center button is being released.
     *
     * @return Is the left center button being released?
     */
    public final boolean isLeftCenterButtonReleased() { return released(7); }

    /**
     * Check if the right center button is being released.
     *
     * @return Is the right center button being released?
     */
    public final boolean isRightCenterButtonReleased() { return released(8); }

    /**
     * Check if the X button is being released.
     *
     * @return Is the X button being released?
     */
    public final boolean isXButtonReleased() { return released(3); }

    /**
     * Check if the Y button is being released.
     *
     * @return Is the Y button being released?
     */
    public final boolean isYButtonReleased() { return released(4); }

    /**
     * Check if the B button is being released.
     *
     * @return Is the B button being released?
     */
    public final boolean isBButtonReleased() { return released(2); }

    /**
     * Check if the A button is being released.
     *
     * @return Is the A button being released?
     */
    public final boolean isAButtonReleased() { return released(1); }

    // * DPAD interface

    public static enum DPADState {
        UP,
        UP_RIGHT,
        RIGHT,
        DOWN_RIGHT,
        DOWN,
        DOWN_LEFT,
        LEFT,
        UP_LEFT;

        public final int getAngle() { return this.ordinal() * 45; }

        public static final DPADState fromAngle(final int angle) { return values()[angle]; }
    }

    /**
     * Get the state of the DPAD.
     *
     * @return The state of the DPAD.
     */
    public final DPADState getDPADState() { return DPADState.fromAngle(stick.getPOV()); }

    /**
     * Check if the the DPAD is at this angle.
     *
     * @param angle The angle to check against.
     *
     * @return Is the DPAD at this angle?
     */
    public final boolean isDPAD(final int angle) { return getDPADState().getAngle() == angle; }

    /**
     * Check if the the DPAD is at this state.
     *
     * @param angle The state to check against.
     *
     * @return Is the DPAD at this state?
     */
    public final boolean isDPAD(final DPADState state) { return getDPADState() == state; }

    /**
     * Check if the DPAD up button is being held down.
     *
     * @return Is the DPAD up button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isUpDown()" method instead
     */
    @Deprecated
    public final boolean getDPADUp() {
        return isDPADUpDown();
    }

    /**
     * Check if the DPAD up right button is being held down.
     *
     * @return Is the DPAD up right button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isUpRightDown()" method instead
     */
    @Deprecated
    public final boolean getDPADUpRight() {
        return isDPADUpRightDown();
    }

    /**
     * Check if the DPAD right button is being held down.
     *
     * @return Is the DPAD right button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isRightDown()" method instead
     */
    @Deprecated
    public final boolean getDPADRight() {
        return isDPADRightDown();
    }

    /**
     * Check if the DPAD down right button is being held down.
     *
     * @return Is the DPAD down right button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isDownRightDown()" method instead
     */
    @Deprecated
    public final boolean getDPADDownRight() {
        return isDPADDownRightDown();
    }

    /**
     * Check if the DPAD down button is being held down.
     *
     * @return Is the DPAD down button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isDownDown()" method instead
     */
    @Deprecated
    public final boolean getDPADDown() {
        return isDPADDownDown();
    }

    /**
     * Check if the DPAD down left button is being held down.
     *
     * @return Is the DPAD down left button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isDownLeftDown()" method instead
     */
    @Deprecated
    public final boolean getDPADDownLeft() {
        return isDPADDownLeftDown();
    }

    /**
     * Check if the DPAD left button is being held down.
     *
     * @return Is the DPAD left button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isLeftDown()" method instead
     */
    @Deprecated
    public final boolean getDPADLeft() {
        return isDPADLeftDown();
    }

    /**
     * Check if the DPAD up left button is being held down.
     *
     * @return Is the DPAD up left button being held down?
     *
     * @deprecated Here for old API compatibility.
     *             Use the "isUpLeftDown()" method instead
     */
    @Deprecated
    public final boolean getDPADUpLeft() {
        return isDPADUpLeftDown();
    }

    /**
     * Check if the DPAD up button is being held down.
     *
     * @return Is the DPAD up button being held down?
     */
    public final boolean isDPADUpDown() { return stick.getPOV() == 0; }

    /**
     * Check if the DPAD up right button is being held down.
     *
     * @return Is the DPAD up right button being held down?
     */
    public final boolean isDPADUpRightDown() { return stick.getPOV() == 45; }

    /**
     * Check if the DPAD right button is being held down.
     *
     * @return Is the DPAD right button being held down?
     */
    public final boolean isDPADRightDown() { return stick.getPOV() == 90; }

    /**
     * Check if the DPAD down right button is being held down.
     *
     * @return Is the DPAD down right button being held down?
     */
    public final boolean isDPADDownRightDown() { return stick.getPOV() == 135; }

    /**
     * Check if the DPAD down button is being held down.
     *
     * @return Is the DPAD down button being held down?
     */
    public final boolean isDPADDownDown() { return stick.getPOV() == 180; }

    /**
     * Check if the DPAD down left button is being held down.
     *
     * @return Is the DPAD down left button being held down?
     */
    public final boolean isDPADDownLeftDown() { return stick.getPOV() == 225; }

    /**
     * Check if the DPAD left button is being held down.
     *
     * @return Is the DPAD left button being held down?
     */
    public final boolean isDPADLeftDown() { return stick.getPOV() == 270; }

    /**
     * Check if the DPAD up left button is being held down.
     *
     * @return Is the DPAD up left button being held down?
     */
    public final boolean isDPADUpLeftDown() { return stick.getPOV() == 315; }

    // * Rumble interface

    /**
     * Set weather or not to rumble the left side.
     *
     * @param rumble To rumble or not to rumble.
     */
    public final void setRumbleLeft(final boolean rumble) {
        stick.setRumble(Joystick.RumbleType.kLeftRumble, rumble ? 1 : 0);
    }

    /**
     * Set weather or not to rumble the right side.
     *
     * @param rumble To rumble or not to rumble.
     */
    public final void setRumbleRight(final boolean rumble) {
        stick.setRumble(Joystick.RumbleType.kRightRumble, rumble ? 1 : 0);
    }

    /**
     * Set weather or not to rumble both sides.
     *
     * @param rumble To rumble or not to rumble.
     */
    public final void setRumble(final boolean rumble) {
        setRumbleLeft(rumble);
        setRumbleRight(rumble);
    }

    public final ArrayList<Method> getActiveBooleanMethods() {
        final ArrayList<Method> methods = new ArrayList<Method>();
        for (final Method method : TorqueController.class.getMethods())
            if (method.getReturnType() == Boolean.class) try {
                    if (method.invoke(this).equals(true)) methods.add(method);
                } catch (final Exception e) { e.printStackTrace(); }
        return methods;
    }

    public final void logActiveBooleanMethods() {
        final StringBuilder sb = new StringBuilder();
        for (final Method method : getActiveBooleanMethods()) sb.append(method.getName()).append(" ");
        SmartDashboard.putString("Active Booleans", sb.toString());
    }
}
