/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.sensors;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;

/**
 * An extended wrapper class for the NavX gyro.
 *
 * Also represents the NavX as a singleton object.
 *
 * @author Justus Languell
 * @author Jack Pittenger
 */
public final class TorqueNavXGyro extends AHRS {
    private static volatile TorqueNavXGyro instance;

    private double angleOffset = 0;

    private TorqueNavXGyro() {
        super(SPI.Port.kMXP);
        getFusedHeading();
    }

    public final void setAngleOffset(final double angleOffset) {
        this.angleOffset = (angleOffset - getFusedHeading() + 360) % 360;
    }

    public final double getAngleOffset() { return angleOffset; }

    public final double getDegreesClockwise() { return (getFusedHeading() + angleOffset) % 360; }

    public final double getDegreesCounterClockwise() { return 360 - getDegreesClockwise(); }

    @Override
    public final Rotation2d getRotation2d() {
        return getRotation2dClockwise();
    }

    public final Rotation2d getRotation2dClockwise() { return Rotation2d.fromDegrees(getDegreesClockwise()); }

    public final Rotation2d getRotation2dCounterClockwise() {
        return Rotation2d.fromDegrees(getDegreesCounterClockwise());
    }

    public static final synchronized TorqueNavXGyro getInstance() {
        return instance == null ? instance = new TorqueNavXGyro() : instance;
    }
}
