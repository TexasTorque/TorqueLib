package org.texastorque.torquelib.control;

import org.texastorque.subsystems.Drivebase;
import org.texastorque.torquelib.swerve.TorqueSwerveSpeeds;

import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.DriverStation;

public class TorqueDriveController {

	private final ProfiledPIDController xController, yController, thetaController;
	private Pose2d lastTargetPose;

	public TorqueDriveController(final PIDConstants translationConstants, final TrapezoidProfile.Constraints translationConstraints, final PIDConstants rotationConstants, final TrapezoidProfile.Constraints rotationConstraints) {
		this.xController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.yController = new ProfiledPIDController(translationConstants.kP, translationConstants.kI, translationConstants.kD, translationConstraints);
		this.thetaController = new ProfiledPIDController(rotationConstants.kP, rotationConstants.kI, rotationConstants.kD, rotationConstraints);
		this.thetaController.enableContinuousInput(-Math.PI, Math.PI);
		this.lastTargetPose = new Pose2d();
	}

	public TorqueSwerveSpeeds calculate(final Pose2d currentPose, final Pose2d targetPose) {
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

	public void reset() {
		Pose2d currentPose = Drivebase.getInstance().getPose();
		ChassisSpeeds speeds = null;

		System.out.println("RESET");
		
		if (DriverStation.isAutonomous()) {
			speeds = TorqueSwerveSpeeds.fromRobotRelativeSpeeds(Drivebase.getInstance().lastRealInputSpeeds, currentPose.getRotation());
		} else {
			speeds = TorqueSwerveSpeeds.fromRobotRelativeSpeeds(Drivebase.getInstance().inputSpeeds, currentPose.getRotation());
		}
		System.out.println(speeds);
		xController.reset(currentPose.getX(), speeds.vxMetersPerSecond);
		yController.reset(currentPose.getY(), speeds.vyMetersPerSecond);
		thetaController.reset(currentPose.getRotation().getRadians(), speeds.omegaRadiansPerSecond);
	}
}