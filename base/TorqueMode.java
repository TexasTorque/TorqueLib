package org.texastorque.torquelib.base;

/**
 * An enum to describe the state of the robot.
 *
 * @author Justus Languell
 */
public enum TorqueMode {
    DISABLED,
    AUTO,
    TELEOP,
    TEST;

    public final boolean isCompetition() { return this == AUTO || this == TELEOP; }

    public final boolean isDisabled() { return this == DISABLED; }

    public final boolean isTeleop() { return this == TELEOP; }

    public final boolean isAuto() { return this == AUTO; }
}
