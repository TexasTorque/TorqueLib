package org.texastorque.torquelib.control;

import org.littletonrobotics.junction.Logger;
import org.texastorque.Subsystems;
import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.Debug;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;

public class TorqueDriveController implements Subsystems {

	private final ProfiledPIDController xController, yController, thetaController;

	public TorqueDriveController(final PIDConstants translationConstants, final TrapezoidProfile.Constraints translationConstraints, final PIDConstants rotationConstants, final TrapezoidProfile.Constraints rotationConstraints) {
		this.xController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.yController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.thetaController = new ProfiledPIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD, rotationConstraints);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
	}

	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose) {
		Debug.log("Align Target Pose", targetPose.toString());
		Logger.recordOutput("Align Target Pose", targetPose);

		double xPower = xController.calculate(currentPose.getX(), targetPose.getX());
		double yPower = yController.calculate(currentPose.getY(), targetPose.getY());
		double thetaPower = thetaController.calculate(currentPose.getRotation().getRadians(), targetPose.getRotation().getRadians());

		TorqueSwerveSpeeds speeds = new TorqueSwerveSpeeds(xPower, yPower, thetaPower);

		return speeds;
	}

	public void reset() {
		Pose2d currentPose = Drivebase.getInstance().getPose();

		ChassisSpeeds currentSpeeds = drivebase.kinematics.toChassisSpeeds(drivebase.getModuleStates());

		xController.reset(currentPose.getX(), currentSpeeds.vxMetersPerSecond);
		yController.reset(currentPose.getY(), currentSpeeds.vyMetersPerSecond);
		thetaController.reset(currentPose.getRotation().getRadians(), currentSpeeds.omegaRadiansPerSecond);
	}
}