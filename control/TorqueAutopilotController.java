package org.texastorque.torquelib.control;

import static edu.wpi.first.units.Units.Centimeters;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.MetersPerSecond;

import org.texastorque.Subsystems;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

import com.therekrab.autopilot.*;
import com.therekrab.autopilot.Autopilot.APResult;

public class TorqueAutopilotController implements Subsystems {

    private final APConstraints contraints;
    private final APProfile profile;
    private final Autopilot autopilot;
    private final PIDController thetaController;

    public TorqueAutopilotController(final PIDConstants rotationPidConstants) {
        this.contraints = new APConstraints()
            .withAcceleration(3.0)
            .withJerk(2.0)
            .withVelocity(1.0);

        this.profile = new APProfile(contraints)
            .withErrorXY(Centimeters.of(2))
            .withErrorTheta(Degrees.of(.5))
            .withBeelineRadius(Centimeters.of(8));

        this.autopilot = new Autopilot(profile);

        this.thetaController = new PIDController(rotationPidConstants.kP, rotationPidConstants.kI, rotationPidConstants.kD);
        thetaController.enableContinuousInput(-Math.PI, Math.PI);
    }

    public static double normalizeTargetRotation(Pose2d currentTagPose) {
        double angularTarget = currentTagPose.getRotation().getDegrees();
        if (!perception.isRedAlliance()) {
            angularTarget -= 180;
            if (angularTarget < 0) angularTarget += 360;
        }
        angularTarget += 90;
        angularTarget %= 360;
        return angularTarget;
    }

    public TorqueSwerveSpeeds calculate(final Pose2d currPose, final Pose2d targetPose, final TorqueSwerveSpeeds currentSpeeds, final double desiredRotation) {

        APTarget target = new APTarget(targetPose)
            .withEntryAngle(Rotation2d.fromDegrees(desiredRotation));

        APResult result = autopilot.calculate(currPose, currentSpeeds, target);
        double radiansPerSecond = thetaController.calculate(currPose.getRotation().getRadians(), targetPose.getRotation().getRadians());
        
        return TorqueSwerveSpeeds.fromFieldRelativeSpeeds(result.vx().in(MetersPerSecond), result.vy().in(MetersPerSecond), radiansPerSecond, result.targetAngle());
    }
}