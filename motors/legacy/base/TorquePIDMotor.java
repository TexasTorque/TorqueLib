/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.motors.legacy.base;

import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.util.KPID;

/**
 * Interface to include PID methods for a PID motor.
 *
 * @author Justus Languell
 */
public interface TorquePIDMotor {
    public final double CLICKS_PER_ROTATION = 0;

    @Deprecated
    public void configurePID(final KPID kPID);

    public void configurePID(final TorquePID pid);

    public void setPosition(final double setpoint);
    public void setPositionDegrees(final double setpoint);
    public void setPositionRotations(final double setpoint);

    public void setVelocity(final double setpoint);
    public void setVelocityRPS(final double setpoint);
    public void setVelocityRPM(final double setpoint);
}
