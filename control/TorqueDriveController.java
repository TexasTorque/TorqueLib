package org.texastorque.torquelib.control;

import org.littletonrobotics.junction.Logger;
import org.texastorque.Subsystems;
import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.Timer;

public class TorqueDriveController implements Subsystems {

	private final ProfiledPIDController xController, yController, thetaController;
	private final Timer timer;

	public TorqueDriveController(final PIDConstants translationConstants, final TrapezoidProfile.Constraints translationConstraints, final PIDConstants rotationConstants, final TrapezoidProfile.Constraints rotationConstraints) {
		this.xController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.yController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.thetaController = new ProfiledPIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD, rotationConstraints);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
		this.timer = new Timer();
	}

	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose) {
		Debug.log("Align Target Pose", targetPose.toString());
		Logger.recordOutput("Align Target Pose", targetPose);

		Pair<Double, Double> offsets = getOffsets(targetPose, currentPose);
		double forward = offsets.getFirst();
		double right = offsets.getSecond();

		if (Math.abs(right) > .01) {
			double xPower = xController.calculate(currentPose.getX(), targetPose.getX());
		}
		
		double xPower = xController.calculate(currentPose.getX(), targetPose.getX());
		double yPower = yController.calculate(currentPose.getY(), targetPose.getY());
		double thetaPower = thetaController.calculate(currentPose.getRotation().getRadians(), targetPose.getRotation().getRadians());

		TorqueSwerveSpeeds speeds = new TorqueSwerveSpeeds(xPower, yPower, thetaPower);
		speeds.toFieldRelativeSpeeds(perception.getHeading());

		return speeds;
	}

	public void reset() {
		Pose2d currentPose = Drivebase.getInstance().getPose();

		ChassisSpeeds currentSpeeds = drivebase.kinematics.toChassisSpeeds(drivebase.getModuleStates());

		xController.reset(currentPose.getX(), currentSpeeds.vxMetersPerSecond);
		yController.reset(currentPose.getY(), currentSpeeds.vyMetersPerSecond);
		thetaController.reset(currentPose.getRotation().getRadians(), currentSpeeds.omegaRadiansPerSecond);
	}

	public Pair<Double, Double> getOffsets(final Pose2d targetPose, final Pose2d currentPose) {
		final double dx = targetPose.getX() - currentPose.getX();
		final double dy = targetPose.getY() - currentPose.getY();

		final double forward = dx * Math.cos(targetPose.getRotation().getRadians()) + dy * Math.sin(targetPose.getRotation().getRadians());
		final double right = -dx * Math.sin(targetPose.getRotation().getRadians()) + dy * Math.cos(targetPose.getRotation().getRadians());

		return new Pair<Double, Double>(forward, right);
	}
}