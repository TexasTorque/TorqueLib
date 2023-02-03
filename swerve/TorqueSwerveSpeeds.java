package org.texastorque.torquelib.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

/**
 * Wrappeer for ChassisSpeeds which has some extra methods.
 * 
 * @author Justus Languell
 */
public final class TorqueSwerveSpeeds extends ChassisSpeeds {
    public TorqueSwerveSpeeds() {
        super(0, 0, 0);
    }

    public TorqueSwerveSpeeds(final double vxMetersPerSecond, final double vyMetersPerSecond, final double omegaRadiansPerSecond) {
        super(vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond);
    }

    public static TorqueSwerveSpeeds fromFieldRelativeSpeeds(
            final double vxMetersPerSecond, final double vyMetersPerSecond, final double omegaRadiansPerSecond,
            final Rotation2d robotRotation) {
        return new TorqueSwerveSpeeds(vxMetersPerSecond * robotRotation.getCos() + vyMetersPerSecond * robotRotation.getSin(),
                -vxMetersPerSecond * robotRotation.getSin() + vyMetersPerSecond * robotRotation.getCos(),
                omegaRadiansPerSecond);
    }

    public static TorqueSwerveSpeeds fromFieldRelativeSpeeds(final TorqueSwerveSpeeds fieldRelativeSpeeds, final Rotation2d robotRotation) {
        return fromFieldRelativeSpeeds(
            fieldRelativeSpeeds.vxMetersPerSecond,
            fieldRelativeSpeeds.vyMetersPerSecond,
            fieldRelativeSpeeds.omegaRadiansPerSecond,
            robotRotation);
    }

    public TorqueSwerveSpeeds toFieldRelativeSpeeds(final Rotation2d robotRotation) {
        return fromFieldRelativeSpeeds(this, robotRotation);
    }

    public final double getVelocityMagnitude() {
        return Math.sqrt(vxMetersPerSecond * vxMetersPerSecond + vyMetersPerSecond * vyMetersPerSecond);
    }

    public final Rotation2d getHeading() {
        return Rotation2d.fromRadians(Math.atan2(vyMetersPerSecond, vxMetersPerSecond));
    }

    public final Transform2d getTransformAtTime(final double seconds) {
        return new Transform2d(
            new Translation2d(vxMetersPerSecond * seconds, vyMetersPerSecond * seconds),
            new Rotation2d(omegaRadiansPerSecond * seconds));
    }

    public final String toString() {
        return String.format("(%f, %f, %f)", vxMetersPerSecond, vyMetersPerSecond, omegaRadiansPerSecond);
    }
}
