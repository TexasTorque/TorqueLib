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
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
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
    private PathPlannerTrajectory trajectory;
    private final Timer timer = new Timer();
    private Translation2d prevTranslation = new Translation2d();

    private final TorquePathingDrivebase drivebase;

    private final PIDController xController, yController, omegaControllerPP;
    private final ProfiledPIDController omegaController;

    private final HolonomicDriveController driveController;
    private final PPHolonomicDriveController driveControllerPP;

    public TorqueFollowPath(final String pathName, final TorquePathingDrivebase drivebase) {
        this(() -> PathPlannerPath.fromPathFile(pathName), drivebase);
    }

    public TorqueFollowPath(final Supplier<PathPlannerPath> pathSupplier, final TorquePathingDrivebase drivebase) {

        xController = new PIDController(1, 0, 0);
        yController = new PIDController(1, 0, 0);

        final Constraints omegaConstraints = new Constraints(Math.PI, Math.PI);

        omegaController = new ProfiledPIDController(Math.PI * 2, 0, .0, omegaConstraints);

        omegaControllerPP = new PIDController(Math.PI, 0, 0);

        // omegaController.setTolerance(Units.degreesToRadians(2));
        // This is a possible problem ... if rotation is geekin change the args to (0,
        // 2*Math.PI)
        omegaController.enableContinuousInput(0, 2 * Math.PI);

        driveController = new HolonomicDriveController(xController, yController, omegaController);

        driveControllerPP = new PPHolonomicDriveController(
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

        // final Pose2d startingPose = trajectory.getInitialTargetHolonomicPose();
        // drivebase.setPose(startingPose);
        drivebase.onBeginPathing();
        timer.start();
    }

    @Override
    protected final void continuous() {
        final double elapsed = timer.get();

        final PathPlannerTrajectory.State desired = trajectory.sample(elapsed);

        // final ChassisSpeeds outputSpeeds =
        // driveController.calculate(drivebase.getPose(),
        // desired.getTargetHolonomicPose(), desired.velocityMps, desired.heading);
        final ChassisSpeeds outputSpeeds = driveControllerPP.calculateRobotRelativeSpeeds(drivebase.getPose(), desired);

        final TorqueSwerveSpeeds realSpeeds = TorqueSwerveSpeeds.fromChassisSpeeds(outputSpeeds);

        // drivebase.setInputSpeeds(realSpeeds.times(-1));
        drivebase.setInputSpeeds(realSpeeds);

        PPLibTelemetry.setCurrentPose(drivebase.getPose());
        PPLibTelemetry.setTargetPose(desired.getTargetHolonomicPose());
        PPLibTelemetry.setPathInaccuracy(prevTranslation.getDistance(drivebase.getPose().getTranslation()));
    }

    @Override
    protected final boolean endCondition() {
        // return trajectory.getEndState().getTargetHolonomicPose().getTranslation()
        // .getDistance(drivebase.getPose().getTranslation()) <= .1
        // ||
        return timer.hasElapsed(trajectory.getTotalTimeSeconds());
    }

    @Override
    protected final void end() {
        drivebase.onEndPathing();
        timer.stop();
        drivebase.setInputSpeeds(new TorqueSwerveSpeeds());
    }

}