/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.sensors;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

/**
 * Java client interface for the TorqueLight.
 *
 * TorqueLight is a vision camera developed by Texas Torque
 * that runs PhotonVision.
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueLight {
    private final PhotonCamera cam;

    private PhotonPipelineResult result;
    private PhotonTrackedTarget target;

    /**
     * Creates a new TorqueLight object with desired physical parameters.
     */
    public TorqueLight() {
        this.cam = new PhotonCamera(NetworkTableInstance.getDefault(), "torquecam");
        NetworkTableInstance.getDefault().getTable("photonvision").getEntry("ledMode").setNumber(1);
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();
    }

    /**
     * Call this periodically to update the vision state.
     */
    public final void update() {
        result = cam.getLatestResult();
        if (result.hasTargets()) target = result.getBestTarget();
    }

    /**
     * Does the camera have any targets.
     *
     * @return Do we have targets or not.
     */
    public final boolean hasTargets() { return result.hasTargets(); }

    /**
     * Returns the area of the target.
     *
     * @return the area of the target.
     */
    public final double getTargetArea() { return target.getArea(); }

    /**
     * Returns the yaw angle of the target as degrees.
     *
     * @return The yaw angle of the target as degrees.
     */
    public final double getTargetYaw() { return target.getYaw(); }

    /**
     * Returns the pitch angle of the target as degrees.
     *
     * @return The pitch angle of the target as degrees.
     */
    public final double getTargetPitch() { return target.getPitch(); }

    /**
     * Returns a transformation that represents the distance
     * betweeen the camera and the target.
     *
     * @return Transformation2d object that represents the
     *         distance betweeen the camera and the target.
     */
    public final Transform2d getCameraToTarget() {
        return target.getCameraToTarget();
    }

    /**
     * 
     * @return The number of targets that the camera detects.
     */
    public final double getNumberOfTargets() {
        return result.getTargets().size();
    }
    
    /**
     * Calculates the distance to the target in meters.
     *
     * @return The distance to the target in meters.
     */
    public static final double getDistanceToElevatedTarget(final TorqueLight camera, 
            final double cameraHeight, final double targetHeight, final Rotation2d angle) {
        return PhotonUtils.calculateDistanceToTargetMeters(cameraHeight, targetHeight, angle.getRadians(),
                Units.degreesToRadians(camera.getTargetPitch()));
    }

    /**
     * Gets the estimated robot position using vision calculations.
     * 
     * @param camera The camera object we are using.
     * @param theta_r Robot's CCW rotation w/ respect to the field.
     * @param theta_dp Camera detection pitch.
     * @param theta_dy Camera detection yaw.
     * @param H_h Height (m) of the targets.
     * @param H_c Height (m) of the camera.
     * @param theta_cp Camera mount's pitch.
     * @param r_tc Radius of the turret + camera.
     * @param theta_t Polar degree rotation of the turret relative the camera.
     * @param r_H Radius of the hub.
     * @param x_H X position of the hub.
     * @param y_H Y position of the hub.
     */
    public final Pose2d getRobotPose(final TorqueLight camera, final Rotation2d theta_r, final Rotation2d theta_dp, 
            final Rotation2d theta_dy, final double H_h, final double H_c, final Rotation2d theta_cp, final double r_tc,
            final double theta_t, final double r_H, final double x_h, final double y_h) {
        final double d = (H_h - H_c) / Math.tan(theta_dp.getRadians() + theta_cp.getRadians()) + r_H - r_tc;
        final double theta_f = theta_r.getRadians() + theta_dp.getRadians() + theta_dy.getRadians();
        final double x_r = x_h - (Math.cos(theta_f) * d);
        final double y_r = y_h - (Math.sin(theta_f) * d);
        return new Pose2d(x_r, y_r, theta_r);
    }

    /**
     * Reads the camera latency in milliseconds.
     * 
     * @return The camera latency in milliseconds
     */
    public final double getLatency() {
        return result.getLatencyMillis();
    }
}
