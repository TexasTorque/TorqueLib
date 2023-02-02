/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
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

    public final void onAuto(final Runnable r) { if (isAuto()) r.run(); }

    public final void onTeleop(final Runnable r) { if (isTeleop()) r.run(); }

    public final void onCompetition(final Runnable r) { if (isCompetition()) r.run(); }

    public final void onDisabled(final Runnable r) { if (isDisabled()) r.run(); }
}