/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PPLibTelemetry;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public final class TorqueFollowPath extends TorqueCommand {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();

        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);
    }

    private final PIDConstants translationConstants = new PIDConstants(1, 0, 0);
    private final PIDConstants rotationConstants = new PIDConstants(Math.PI * 2.5, 0, 0);

    private final PPHolonomicDriveController controller;

    private final PathPlannerPath path;
    private PathPlannerTrajectory trajectory;
    private final Timer timer = new Timer();

    private final TorquePathingDrivebase drivebase;

    public TorqueFollowPath(final TorquePathingDrivebase drivebase, final String pathName,
            final ChassisSpeeds initalSpeeds,
            final Rotation2d initialHeading, final double maxModuleSpeed, final double drivebaseRadius) {
        this.drivebase = drivebase;

        controller = new PPHolonomicDriveController(translationConstants, rotationConstants, maxModuleSpeed,
                drivebaseRadius);

        path = PathPlannerPath.fromPathFile(pathName);
        trajectory = new PathPlannerTrajectory(path, initalSpeeds, initialHeading);
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

        final State desired = trajectory.sample(elapsed);

        final TorqueSwerveSpeeds speeds = TorqueSwerveSpeeds
                .fromChassisSpeeds(controller.calculateRobotRelativeSpeeds(drivebase.getPose(), desired));

        drivebase.setInputSpeeds(speeds.times(-1, -1, 1));

        PPLibTelemetry.setCurrentPose(drivebase.getPose());
        PPLibTelemetry.setTargetPose(desired.getTargetHolonomicPose());
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