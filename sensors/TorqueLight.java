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
    public final Transform2d getCameraToTarget() { return target.getCameraToTarget(); }

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
     * Reads the camera latency in milliseconds.
     * 
     * @return The camera latency in milliseconds
     */
    public final double getLatency() {
        return result.getLatencyMillis();
    }
}
