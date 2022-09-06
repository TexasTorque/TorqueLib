/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * Replacment for SmartDashboard.
 *
 * Am going to experiment with fancy ShuffleBoardd stuff.
 *
 * @author Justus Languell
 */
public final class TorqueLogging {

    // public static final ShuffleboardTab tab = Shuffleboard.getTab("Texas Torque");

    public static final String TABLE_IDENTIFIER = "Log";

    public static final String FORMAT_DECIMAL = "%03.3f";
    public static final String FORMAT_POSE2D =
            String.format("(%s, %s) %s°", FORMAT_DECIMAL, FORMAT_DECIMAL, FORMAT_DECIMAL);
    public static final String FORMAT_CHASSIS_SPEEDS = FORMAT_POSE2D;

    private static final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
    private static final NetworkTable table = ntInstance.getTable(TABLE_IDENTIFIER);

    private TorqueLogging() { TorqueUtil.staticConstructor(); }

    @Deprecated
    public static final void putString(final String identifier, final String value) {
        table.getEntry(identifier).setString(value);
    }

    @Deprecated
    public static final void putNumber(final String identifier, final double value) {
        putString(identifier, String.format(FORMAT_DECIMAL, value));
    }

    @Deprecated
    public static final void putNumber(final String identifier, final double... values) {
        final StringBuilder builder = new StringBuilder();
        for (final double value : values) builder.append(String.format(FORMAT_DECIMAL + " ", value));
        putString(identifier, builder.toString());
    }

    @Deprecated
    public static final void putPose2d(final String identifier, final Pose2d value) {
        putString(identifier,
                  String.format(FORMAT_POSE2D, value.getX(), value.getY(), value.getRotation().getDegrees()));
    }

    @Deprecated
    public static final void putChassisSpeeds(final String identifier, final ChassisSpeeds value) {
        putString(identifier, String.format(FORMAT_CHASSIS_SPEEDS, value.vxMetersPerSecond, value.vyMetersPerSecond,
                                            value.omegaRadiansPerSecond));
    }

    @Deprecated
    public static final void putBoolean(final String identifier, final boolean value) {
        table.getEntry(identifier).setBoolean(value);
    }
}
