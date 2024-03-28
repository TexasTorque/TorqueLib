/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.swerve.base;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve module base class
 */
public abstract class TorqueSwerveModule {
    protected final String name;

    protected TorqueSwerveModule(final String name) {
        this.name = name.replaceAll(" ", "_").toLowerCase();
    }

    public String getPort() {
        return name;
    }

    public abstract void setDesiredState(final SwerveModuleState state);

    public abstract SwerveModuleState getState();

    public abstract SwerveModulePosition getPosition();

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
