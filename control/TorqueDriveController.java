package org.texastorque.torquelib.control;

import org.littletonrobotics.junction.Logger;
import org.texastorque.Subsystems;
import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Timer;

public class TorqueDriveController implements Subsystems {

	private final PIDController xController, yController, thetaController;
	private final Timer timer;
	
	public TorqueDriveController(final PIDConstants translationConstants, final PIDConstants rotationConstants) {
		this.xController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD);
		this.yController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD);
		this.thetaController = new PIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
		this.timer = new Timer();
	}
	
	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose) {

		Pair<Double, Double> offsets = getOffsets(targetPose, currentPose);
		double forward = offsets.getFirst();
		double right = offsets.getSecond();
		double desiredForward = forward;
		double desiredRight = right;
		double slope = .3;
		Pose2d desiredPose;
	
		desiredForward *= slope;

		desiredPose = new Pose2d(
			currentPose.getX() + (desiredForward * Math.cos(targetPose.getRotation().getRadians())) + (desiredRight * Math.cos(targetPose.getRotation().getRadians() + Math.PI / 2)),
			currentPose.getY() + (desiredForward * Math.sin(targetPose.getRotation().getRadians())) + (desiredRight * Math.sin(targetPose.getRotation().getRadians() + Math.PI / 2)),
			targetPose.getRotation()
		);
		
		double xPower = xController.calculate(currentPose.getX(), desiredPose.getX());
		double yPower = yController.calculate(currentPose.getY(), desiredPose.getY());
		double thetaPower = thetaController.calculate(currentPose.getRotation().getRadians(), desiredPose.getRotation().getRadians());

		TorqueSwerveSpeeds speeds = new TorqueSwerveSpeeds(xPower, yPower, thetaPower);

		Debug.log("Align Target Pose", desiredPose.toString());
		Logger.recordOutput("Align Target Pose", desiredPose);

		return speeds;
	}

	public void reset() {
		// Pose2d currentPose = Drivebase.getInstance().getPose();
		// ChassisSpeeds currentSpeeds = ChassisSpeeds.fromRobotRelativeSpeeds(drivebase.inputSpeeds, currentPose.getRotation());

		// xController.reset(currentPose.getX(), currentSpeeds.vxMetersPerSecond);
		// yController.reset(currentPose.getY(), currentSpeeds.vyMetersPerSecond);
		// thetaController.reset(currentPose.getRotation().getRadians(), currentSpeeds.omegaRadiansPerSecond);
	}

	public Pair<Double, Double> getOffsets(final Pose2d targetPose, final Pose2d currentPose) {
		final double dx = targetPose.getX() - currentPose.getX();
		final double dy = targetPose.getY() - currentPose.getY();

		final double forward = dx * Math.cos(targetPose.getRotation().getRadians()) + dy * Math.sin(targetPose.getRotation().getRadians());
		final double right = -dx * Math.sin(targetPose.getRotation().getRadians()) + dy * Math.cos(targetPose.getRotation().getRadians());

		return new Pair<Double, Double>(forward, right);
	}
}