package org.texastorque.torquelib.util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Replacment for SmartDashboard.
 * 
 * @author Justus Languell
 */
public final class TorqueLogging {
    public static final String TABLE_IDENTIFIER = "Log";

    public static final String FORMAT_DECIMAL = "%03.3f";
    public static final String FORMAT_POSE2D = String.format("(%s, %s) %s°", 
            FORMAT_DECIMAL, FORMAT_DECIMAL, FORMAT_DECIMAL);

    private static final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
    private static final NetworkTable table = ntInstance.getTable(TABLE_IDENTIFIER);

    private TorqueLogging() { TorqueMiscUtil.staticConstructor(); }

    public static final void putString(final String identifier, final String value) {
        table.getEntry(identifier).setString(value);
    }

    public static final void putNumber(final String identifier, final double value) {
        putString(identifier, String.format(FORMAT_DECIMAL, value));
    }

    public static final void putNumber(final String identifier, final double... values) {
        final StringBuilder builder = new StringBuilder();
        for (final double value : values) builder.append(String.format(FORMAT_DECIMAL, value));
        putString(identifier, builder.toString());
    }

    public static final void putPose2d(final String identifier, final Pose2d value) {
        putString(identifier, String.format(FORMAT_POSE2D, value.getX(), value.getY(), value.getRotation().getDegrees())); 
    }

    public static final void putBoolean(final String identifier, final boolean value) {
        table.getEntry(identifier).setBoolean(value);
    }
}
