/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.Supplier;

import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PPLibTelemetry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public final class TorqueFollowPath extends TorqueCommand {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();

        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);

        public void onBeginPathing();

        public void onEndPathing();
    }

    private final Supplier<PathPlannerPath> pathSupplier;
    private final Timer timer = new Timer();
    private final TorquePathingDrivebase drivebase;
    private final PPHolonomicDriveController driveController;

    private PathPlannerTrajectory trajectory;
    private Translation2d prevTranslation = new Translation2d();

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase) {
        this(() -> PathPlannerPath.fromPathFile(pathName), drivebase);
    }

    public TorqueFollowPath(final Supplier<PathPlannerPath> pathSupplier, final TorquePathingDrivebase drivebase) {
        driveController = new PPHolonomicDriveController(
                new PIDConstants(1, 0, 0),
                new PIDConstants(Math.PI, 0, 0),
                3, Drivebase.WIDTH * Math.sqrt(2));

        this.drivebase = drivebase;
        this.pathSupplier = pathSupplier;
    }

    @Override
    protected final void init() {
        timer.reset();

        final PathPlannerPath path = pathSupplier.get();
        this.trajectory = path.getTrajectory(new ChassisSpeeds(), drivebase.getPose().getRotation());
        PPLibTelemetry.setCurrentPath(path);

        final Pose2d startingPose = trajectory.getInitialTargetHolonomicPose();
        drivebase.setPose(startingPose);
        drivebase.onBeginPathing();
        timer.start();
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        final PathPlannerTrajectory.State desired = trajectory.sample(elapsed);

        final ChassisSpeeds outputSpeeds = driveController.calculateRobotRelativeSpeeds(drivebase.getPose(), desired);

        final TorqueSwerveSpeeds realSpeeds = TorqueSwerveSpeeds.fromChassisSpeeds(outputSpeeds);

        drivebase.setInputSpeeds(realSpeeds);

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
        drivebase.onEndPathing();
        timer.stop();
        drivebase.setInputSpeeds(new TorqueSwerveSpeeds());
    }

}