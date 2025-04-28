package org.texastorque.torquelib.control;

import org.littletonrobotics.junction.Logger;
import org.texastorque.Subsystems;
import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;
import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;

public class TorqueAlignController implements Subsystems {

	private final PIDController xController, yController, thetaController;
	
	public TorqueAlignController(final PIDConstants translationConstants, final PIDConstants rotationConstants) {
		this.xController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD);
		this.yController = new PIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD);
		this.thetaController = new PIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
	}

	public Pair<Double, Double> getOffsets(final Pose2d targetPose, final Pose2d currentPose) {
		final double dx = targetPose.getX() - currentPose.getX();
		final double dy = targetPose.getY() - currentPose.getY();

		final double forward = dx * Math.cos(targetPose.getRotation().getRadians()) + dy * Math.sin(targetPose.getRotation().getRadians());
		final double right = -dx * Math.sin(targetPose.getRotation().getRadians()) + dy * Math.cos(targetPose.getRotation().getRadians());

		return new Pair<Double, Double>(forward, right);
	}
	
	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose, boolean sidewaysFirst) {
		final Pair<Double, Double> offsets = getOffsets(targetPose, currentPose);
		double desiredForward = offsets.getFirst();
		double desiredRight = offsets.getSecond();
		final double SLOPE = .3;
	
		if (sidewaysFirst) desiredForward *= SLOPE;

		Pose2d desiredPose = new Pose2d(
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
}