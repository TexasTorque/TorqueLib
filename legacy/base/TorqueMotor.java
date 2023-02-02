/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.legacy.base;

/**
 * A base class for Texas Torque motor controller wrappers.
 *
 * @author Justus Languell
 */
public abstract class TorqueMotor {
    protected final int port;

    protected TorqueMotor(final int port) { this.port = port; }

    public abstract void setPercent(final double percent);
    public abstract void setVoltage(final double percent);

    public abstract void addFollower(final int port);
    public abstract void addFollower(final int port, final boolean invert);

    public abstract void invert(final boolean invert);
}
