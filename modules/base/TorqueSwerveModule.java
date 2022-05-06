package org.texastorque.torquelib.modules.base;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public abstract class TorqueSwerveModule {
    protected final int id;

    protected TorqueSwerveModule(final int id) {
        this.id = id;
    }

    public int getPort() {
        return id;
    }

    public abstract void setDesiredState(final SwerveModuleState state);
    public abstract SwerveModuleState getState();
    public abstract Rotation2d getRotation();
}
