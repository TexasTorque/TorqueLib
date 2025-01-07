/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.Supplier;

import org.texastorque.auto.AutoManager;
import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.trajectory.PathPlannerTrajectory;
import com.pathplanner.lib.trajectory.PathPlannerTrajectoryState;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.util.PPLibTelemetry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public final class TorqueFollowPath extends TorqueCommand {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();

        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);

        public ChassisSpeeds getActualChassisSpeeds();

        public void onBeginPathing();

        public void onEndPathing();

        public double getRadius();
        public double getMaxPathingVelocity();
    }

    private final Supplier<PathPlannerPath> pathSupplier;
    private final Timer timer = new Timer();
    private final TorquePathingDrivebase drivebase;
    private final PPHolonomicDriveController driveController;
    private final RobotConfig config;

    private PathPlannerTrajectory trajectory;

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase) {
        this(pathName, drivebase, AutoManager.getRobotConfig());
    }

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase, final RobotConfig config) {
        this(() -> {
            try {
                return PathPlannerPath.fromPathFile(pathName);
            } catch (Exception e) {
                System.out.println("Failed to load path: " + pathName);
            }
            return null;
        }, drivebase, config);
    }

    public TorqueFollowPath(final Supplier<PathPlannerPath> pathSupplier, final TorquePathingDrivebase drivebase, final RobotConfig config) {
        driveController = new PPHolonomicDriveController(
                new PIDConstants(10, 0, 0),
                new PIDConstants(Math.PI, 0, 0));
        
        this.drivebase = drivebase;
        this.pathSupplier = pathSupplier;
        this.config = config;
    }

    private static Pose2d endPosition = new Pose2d();

    public static Pose2d getEndingPositionForCurrentlyLoadedPath() {
        return endPosition;
    }

    @Override
    protected final void init() {
        PathPlannerPath path = pathSupplier.get();

        if (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == DriverStation.Alliance.Red)
            path = path.flipPath();

        driveController.reset(drivebase.getPose(), drivebase.getActualChassisSpeeds());

        this.trajectory = path.generateTrajectory(new ChassisSpeeds(), drivebase.getPose().getRotation(), config);
        endPosition = trajectory.getEndState().pose;

        PPLibTelemetry.setCurrentPath(path);

        final Pose2d startingPose = trajectory.getInitialPose();

        drivebase.setPose(startingPose);
        drivebase.onBeginPathing();
        timer.restart();
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        final PathPlannerTrajectoryState desired = trajectory.sample(elapsed);

        final ChassisSpeeds outputSpeeds = driveController.calculateRobotRelativeSpeeds(drivebase.getPose(), desired);

        final TorqueSwerveSpeeds realSpeeds = TorqueSwerveSpeeds.fromChassisSpeeds(outputSpeeds);

        drivebase.setInputSpeeds(realSpeeds);

        PPLibTelemetry.setCurrentPose(drivebase.getPose());
        PPLibTelemetry.setTargetPose(desired.pose);
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