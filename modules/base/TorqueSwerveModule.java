/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.modules.base;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Swerve module base class
 */
public abstract class TorqueSwerveModule {
    protected final int id;

    protected TorqueSwerveModule(final int id) { this.id = id; }

    public int getPort() { return id; }

    public abstract void setDesiredState(final SwerveModuleState state);
    public abstract SwerveModuleState getState();
    public abstract Rotation2d getRotation();
}
