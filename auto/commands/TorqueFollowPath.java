/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.path.EventMarker;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.PathPlannerLogging;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;

public final class TorqueFollowPath extends TorqueCommand {

    public static interface TorquePathingDrivebase {
        public Pose2d getPose();
        public void setPose(final Pose2d pose);

        public void setInputSpeeds(final TorqueSwerveSpeeds speeds);
    }

    public static final double MAX_VELOCITY_PATH = 4, MAX_ACCELERATION_PATH = 2;

    private final PIDController xController = new PIDController(1, 0, 0);
    private final PIDController yController = new PIDController(1, 0, 0);

    private final PIDConstants translationConstants = new PIDConstants(1, 0, 0);
    private final PIDConstants rotationConstants = new PIDConstants(Math.PI * 0.5, 0, 0);

    private final PIDController omegaController;
    private final PPHolonomicDriveController controller;

    final Supplier<PathPlannerTrajectory> path;
    private PathPlannerTrajectory trajectory;
    private final Timer timer = new Timer();

    private List<EventMarker> unpassed, events;
    private final Map<String, TorqueCommand> commands;
    private final List<TorqueCommand> running;

    private final TorquePathingDrivebase drivebase;

    public TorqueFollowPath(final TorquePathingDrivebase drivebase, final Supplier<PathPlannerTrajectory> path,
                final Map<String, TorqueCommand> commands, final double maxModuleSpeed, final double drivebaseRadius) {
        this.drivebase = drivebase;

        omegaController = new PIDController(Math.PI * .5, 0, .0);

        xController.setTolerance(0.01);
        yController.setTolerance(0.01);
        omegaController.setTolerance(Units.degreesToRadians(2));
        omegaController.enableContinuousInput(-Math.PI, Math.PI);

        controller = new PPHolonomicDriveController(translationConstants, rotationConstants, maxModuleSpeed, drivebaseRadius);

        this.path = path;
        events = new ArrayList<EventMarker>();
        unpassed = new ArrayList<EventMarker>();
        this.commands = commands;
        running = new ArrayList<TorqueCommand>();
    }

    @Override
    protected final void init() {
        timer.reset();
        timer.start();

        trajectory = path.get();
        // PathPlannerServer.sendActivePath(this.trajectory.getStates());
        // events = trajectory.getMarkers();
        unpassed.clear();
        unpassed.addAll(events);
        running.clear();

        final Pose2d startingPose = trajectory.getInitialTargetHolonomicPose();
        drivebase.setPose(startingPose);
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        final State desired = trajectory.sample(elapsed);

        final TorqueSwerveSpeeds speeds = TorqueSwerveSpeeds
                .fromChassisSpeeds(controller.calculateRobotRelativeSpeeds(drivebase.getPose(), desired));

        drivebase.setInputSpeeds(speeds.times(1, 1, 1));

        // if (unpassed.size() > 0 && elapsed >= unpassed.get(0).timeSeconds) {
        //     final EventMarker marker = unpassed.remove(0);
        //     for (final String name : marker.names) {
        //         final TorqueCommand command = commands.getOrDefault(name, null);
        //         if (command != null)
        //             running.add(command);
        //     }
        // }

        // for (int i = running.size() - 1; i >= 0; i--)
        //     if (running.get(i).run())
        //         running.remove(i);

        // PathPlannerServer.sendPathFollowingData(
                // new Pose2d(desired.poseMeters.getTranslation(), desired.holonomicRotation), drivebase.getPose());

    }

    @Override
    protected final boolean endCondition() {
        return timer.hasElapsed(trajectory.getTotalTimeSeconds());
    }

    @Override
    protected final void end() {
        timer.stop();

        for (final TorqueCommand command : running)
            command.reset();
        for (final TorqueCommand command : commands.values())
            command.reset();

        drivebase.setInputSpeeds(new TorqueSwerveSpeeds());
    }

}