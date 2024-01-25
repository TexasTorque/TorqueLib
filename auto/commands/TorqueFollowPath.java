/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.Debug;
import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PPLibTelemetry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

public final class TorqueFollowPath extends TorqueCommand {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();

        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);
    }

    private final double ACCELERATION_COEFFICIENT = .2;

    private final PathPlannerPath path;
    private PathPlannerTrajectory trajectory;
    private final Timer timer = new Timer();
    private Translation2d prevTranslation = new Translation2d();

    private final TorquePathingDrivebase drivebase;

    private final Field2d field = new Field2d();

    private final TorqueHolonomicDriveController driveController = new TorqueHolonomicDriveController(
            new PIDConstants(1, 0, 0),
            new PIDConstants(4, 0, 0),
            4,
            Drivebase.WIDTH / 2 * Math.sqrt(2));

    public TorqueFollowPath(final TorquePathingDrivebase drivebase, final String pathName,
            final ChassisSpeeds initalSpeeds,
            final Rotation2d initialHeading, final double maxModuleSpeed, final double drivebaseRadius) {
        this.drivebase = drivebase;

        path = PathPlannerPath.fromPathFile(pathName);
        trajectory = new PathPlannerTrajectory(path, initalSpeeds, initialHeading);
        Debug.field("PATH FIELD", field);
    }

    @Override
    protected final void init() {
        timer.reset();
        timer.start();
        PPLibTelemetry.setCurrentPath(path);

        final Pose2d startingPose = trajectory.getInitialTargetHolonomicPose();
        drivebase.setPose(startingPose);
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        State desired = trajectory.sample(elapsed);
        // desired = TorqueHolonomicDriveController.transformStateForAlliance(desired);

        final Rotation2d desiredHeading = desired.heading;

        field.setRobotPose(desired.getTargetHolonomicPose());

        Debug.log("Requested Heading", desiredHeading.getDegrees());

        final TorqueSwerveSpeeds speeds = TorqueSwerveSpeeds
                .fromChassisSpeeds(driveController.calculateFieldRelativeSpeeds(drivebase.getPose(), desired));

        speeds.vxMetersPerSecond -= desired.accelerationMpsSq * desiredHeading.getCos() * ACCELERATION_COEFFICIENT;
        speeds.vyMetersPerSecond -= desired.accelerationMpsSq * desiredHeading.getSin() * ACCELERATION_COEFFICIENT;

        drivebase.setInputSpeeds(new TorqueSwerveSpeeds(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond,
                speeds.omegaRadiansPerSecond));

        Debug.log("Output Rotational Speed", speeds.omegaRadiansPerSecond);

        prevTranslation = desired.positionMeters;

        PPLibTelemetry.setCurrentPose(drivebase.getPose());
        PPLibTelemetry.setTargetPose(desired.getTargetHolonomicPose());
        PPLibTelemetry.setPathInaccuracy(prevTranslation.getDistance(drivebase.getPose().getTranslation()));
    }

    @Override
    protected final boolean endCondition() {
        return timer.hasElapsed(trajectory.getTotalTimeSeconds());
    }

    @Override
    protected final void end() {
        timer.stop();
        drivebase.setInputSpeeds(new TorqueSwerveSpeeds());
    }

}