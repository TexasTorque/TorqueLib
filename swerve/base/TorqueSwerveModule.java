/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve.base;

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

    public static final class SwervePorts {
        public final int drive, turn, encoder;
    
        public SwervePorts(final int drive, final int turn, final int encoder) {
            this.drive = drive;
            this.turn = turn;
            this.encoder = encoder;
        }
    }
}
