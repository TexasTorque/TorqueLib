package org.texastorque.torquelib.auto.commands;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import org.texastorque.torquelib.base.TorqueRobotBase;

import com.pathplanner.lib.path.GoalEndState;
import com.pathplanner.lib.path.PathConstraints;
import com.pathplanner.lib.path.PathPlannerPath;

// Class adapted from PPHolonomicDriveController from PathplannerLib

import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.path.PathPlannerTrajectory.State;
import com.pathplanner.lib.util.PIDConstants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class TorqueHolonomicDriveController {
    private final PIDController xController;
    private final PIDController yController;
    private final ProfiledPIDController rotationController;
    private final double maxModuleSpeed;
    private final double mpsToRps;
    public static final double FIELD_WIDTH = Units.inchesToMeters(315.5);

    public TorqueHolonomicDriveController(final PIDConstants translationConstants,
     PIDConstants rotationConstants,
        double period, double maxModuleSpeed, double driveBaseRadius) {

    this.xController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD,
        period);
    this.xController.setIntegratorRange(-translationConstants.iZone, translationConstants.iZone);
    this.yController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD,
        period);
    this.yController.setIntegratorRange(-translationConstants.iZone, translationConstants.iZone);
    this.rotationController = new ProfiledPIDController(rotationConstants.kP, rotationConstants.kI,
        rotationConstants.kD, new TrapezoidProfile.Constraints(0.0, 0.0), period);
    this.rotationController.setIntegratorRange(-rotationConstants.iZone, rotationConstants.iZone);
    // This is PI wtf?
    // this.rotationController.enableContinuousInput(-3.141592653589793, Math.PI);
    this.rotationController.enableContinuousInput(-Math.PI, Math.PI);
    this.maxModuleSpeed = maxModuleSpeed;
    this.mpsToRps = 1.0 / driveBaseRadius;
    }

    public TorqueHolonomicDriveController(final PIDConstants translationConstants, final PIDConstants rotationConstants,
        double maxModuleSpeed, double driveBaseRadius) {
        this(translationConstants, rotationConstants, TorqueRobotBase.PERIOD, maxModuleSpeed, driveBaseRadius);
    }

    public void reset(final Pose2d currentPose, final ChassisSpeeds currentSpeeds) {
        this.rotationController.reset(currentPose.getRotation().getRadians(), currentSpeeds.omegaRadiansPerSecond);
    }

    public ChassisSpeeds calculateFieldRelativeSpeeds(final Pose2d currentPose, final PathPlannerTrajectory.State targetState) {
        final double xFF = targetState.velocityMps * targetState.heading.getCos();
        final double yFF = targetState.velocityMps * targetState.heading.getSin();

        final double xFeedback = this.xController.calculate(currentPose.getX(), targetState.positionMeters.getX());
        final double yFeedback = this.yController.calculate(currentPose.getY(), targetState.positionMeters.getY());

        final double angVelConstraint = targetState.constraints.getMaxAngularVelocityRps();
        final double maxAngVelModule = Math.max(0.0, this.maxModuleSpeed - targetState.velocityMps) * this.mpsToRps;

        final double maxAngVel = Math.min(angVelConstraint, maxAngVelModule);
        
        
        final TrapezoidProfile.Constraints rotationConstraints = new TrapezoidProfile.Constraints(maxAngVel,
            targetState.constraints.getMaxAngularAccelerationRpsSq());

        final double targetRotationVel = this.rotationController.calculate(
            currentPose.getRotation().getRadians(),
            new TrapezoidProfile.State(targetState.targetHolonomicRotation.getRadians(), 0.0), rotationConstraints);

        return new ChassisSpeeds(xFF + xFeedback, yFF + yFeedback, targetRotationVel);
    }

    /**
     * THIS IS UNTESTED DO NOT USE YET
     * 
     * @param state
     * @return
     */
    @Deprecated
    public static State transformStateForAlliance(final State state) {
        if (DriverStation.getAlliance().get() ==  Alliance.Blue) return state;
        else {
            State reflected = new State();
            reflected.timeSeconds = state.timeSeconds;
            reflected.velocityMps = state.velocityMps; // invert?
            reflected.accelerationMpsSq = state.accelerationMpsSq; // invert?
            reflected.headingAngularVelocityRps = state.headingAngularVelocityRps;
            reflected.positionMeters = state.positionMeters;
            reflected.heading = state.heading;
            reflected.targetHolonomicRotation = state.targetHolonomicRotation;
            reflected.holonomicAngularVelocityRps = state.holonomicAngularVelocityRps;
            reflected.curvatureRadPerMeter = state.curvatureRadPerMeter;
            reflected.constraints = state.constraints;
            return reflected;
        }
    }

}

