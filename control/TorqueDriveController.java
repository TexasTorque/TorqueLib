package org.texastorque.torquelib.control;

import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class TorqueDriveController {

	private final ProfiledPIDController xController, yController, thetaController;
	private Pose2d lastTargetPose;
	private Pose2d lastCurrentPose;

	public TorqueDriveController(final PIDConstants translationConstants, final TrapezoidProfile.Constraints translationConstraints, final PIDConstants rotationConstants, final TrapezoidProfile.Constraints rotationConstraints) {
		this.xController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.yController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.thetaController = new ProfiledPIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD, rotationConstraints);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
		this.lastTargetPose = new Pose2d();
	}

	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose) {
		lastCurrentPose = currentPose;
		if (!targetPose.equals(lastTargetPose)) {
			reset();
		}

		double xPower = xController.calculate(currentPose.getX(), targetPose.getX());
		double yPower = yController.calculate(currentPose.getY(), targetPose.getY());
		double thetaPower = thetaController.calculate(currentPose.getRotation().getRadians(), targetPose.getRotation().getRadians());

		TorqueSwerveSpeeds speeds = new TorqueSwerveSpeeds(xPower, yPower, thetaPower);

		lastTargetPose = targetPose;

		return speeds;
	}

	private void reset() {
		ChassisSpeeds speeds = TorqueSwerveSpeeds.fromRobotRelativeSpeeds(Drivebase.getInstance().lastRealInputSpeeds, lastCurrentPose.getRotation());
		xController.reset(lastCurrentPose.getX(), speeds.vxMetersPerSecond);
		yController.reset(lastCurrentPose.getY(), speeds.vyMetersPerSecond);
		thetaController.reset(lastCurrentPose.getRotation().getRadians(), speeds.omegaRadiansPerSecond);
	}
}