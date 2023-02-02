/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

/**
 * An enum to represent a three state representation of
 * a direction with a quick multiplier.
 *
 * Implements TorqueSubsystemState for the static logging
 * method.
 *
 * @author Justus
 */
public enum TorqueDirection {
    REVERSE,
    NEUTRAL,
    FORWARD;

    public final double get() { return ordinal() - 1.; }

    public static final TorqueDirection OFF = NEUTRAL;
}
