/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
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
