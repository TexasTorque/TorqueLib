
/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import org.texastorque.Subsystems;
import org.texastorque.auto.AutoManager;
import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.marker.Marker;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PPLibTelemetry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.Trajectory.State;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public final class TorqueFollowPath extends TorqueCommand implements Subsystems {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();

        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);

        public ChassisSpeeds getActualChassisSpeeds();

        public void onBeginPathing();

        public void onEndPathing();

        public double getRadius();

        public double getMaxPathingVelocity();
        
        public void setCurrentTrajectory(final Trajectory trajectory);
    }

    private final Supplier<PathPlannerPath> pathSupplier;
    private final Timer timer = new Timer();
    private final TorquePathingDrivebase drivebase;
    private final PPHolonomicDriveController driveController;
    private Marker[] markers;

    private PathPlannerTrajectory trajectory;
    private Translation2d prevTranslation = new Translation2d();

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase) {
        this(pathName, drivebase, () -> false);
    }

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase, final BooleanSupplier endEarly) {
        this(() -> PathPlannerPath.fromPathFile(pathName), drivebase, endEarly);
    }

    public TorqueFollowPath(final Supplier<PathPlannerPath> pathSupplier, final TorquePathingDrivebase drivebase) {
        this(pathSupplier, drivebase, () -> false);
    }

    public TorqueFollowPath(final Supplier<PathPlannerPath> pathSupplier, final TorquePathingDrivebase drivebase, final BooleanSupplier endEarly) {
        driveController = new PPHolonomicDriveController(
                new PIDConstants(10, 0, 0),
                new PIDConstants(Math.PI, 0, 0),
                drivebase.getMaxPathingVelocity(), drivebase.getRadius());

        this.drivebase = drivebase;
        this.pathSupplier = pathSupplier;
    }

    private static Pose2d endPosition = new Pose2d();

    public static Pose2d getEndingPositionForCurrentlyLoadedPath() {
        return endPosition;
    }

    @Override
    protected final void init() {
        PathPlannerPath path = pathSupplier.get();

        if (DriverStation.getAlliance().isPresent() && DriverStation.getAlliance().get() == DriverStation.Alliance.Red) {
            path = path.flipPath();
            path = AutoManager.shift(path);
        }

        driveController.reset(drivebase.getPose(), drivebase.getActualChassisSpeeds());

        this.trajectory = path.getTrajectory(new ChassisSpeeds(), drivebase.getPose().getRotation());
        endPosition = trajectory.getEndState().getTargetHolonomicPose();

        PPLibTelemetry.setCurrentPath(path);

        final Pose2d startingPose = trajectory.getInitialTargetHolonomicPose();

        drivebase.setPose(startingPose);
        drivebase.onBeginPathing();
        timer.restart();
        drivebase.setCurrentTrajectory(fromPathPlannerTrajectory(trajectory));
    }

    public Trajectory fromPathPlannerTrajectory(final PathPlannerTrajectory trajectory) {
        final List<State> states = new ArrayList<>();
        for (com.pathplanner.lib.path.PathPlannerTrajectory.State state : trajectory.getStates()) {
            states.add(new State(state.timeSeconds, state.velocityMps, state.accelerationMpsSq, state.getTargetHolonomicPose(), state.curvatureRadPerMeter));
        }

        return new Trajectory(states);
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        if (this.markers == null) this.markers = new Marker[0];
        for (Marker marker : markers) {
            if (timer.hasElapsed(trajectory.getTotalTimeSeconds() * marker.getRelativePosition()) && !marker.hasRan()) {
                marker.run();
            }
        }
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

    public TorqueFollowPath withMarkers(final Marker... markers) {
        this.markers = markers;
        return this;
    }
}