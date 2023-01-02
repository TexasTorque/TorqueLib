/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.sensors;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Map;
import java.util.Optional;

import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

/**
 * Client interface for the TorqueLight2.
 * 
 * TorqueLight and TorqueLight2 are vision cameras developed by 
 * Jack Pittenger at Texas Torque that runs PhotonVision.
 * 
 * TorqueLight2 was the updated version for the 2023 season that
 * was designed to specialize in April Tag detection.
 * 
 * The TorqueLight2 class is updated to specialize in AprilTags 
 * and to use the Optional class to avoid null pointer exceptions.
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueLight2 {
    private final PhotonCamera cam;

    private PhotonPipelineResult result;
    private PhotonTrackedTarget target;

    private Transform3d centerToCamera;

    /**
     * Creates a new TorqueLight object with desired table name. 
     * 
     * @param name Table name.
     */
    public TorqueLight2(final String name) {
        this(name, new Transform3d());
    }

    /**
     * Creates a new TorqueLight object with desired table name. 
     * 
     * @param name Table name.
     * @param centerToCamera The transformation from the center to the camera.
     */
    public TorqueLight2(final String name, final Transform3d centerToCamera) {
        this.cam = new PhotonCamera(NetworkTableInstance.getDefault(), name);
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();
        setCenterToCamera(centerToCamera);
    }

    /**
     * Call this periodically to update the vision state.
     */
    public final void update() {
        result = cam.getLatestResult();
        if (result.hasTargets()) target = result.getBestTarget();
    }

    /**
     * Sets the transform to the camera from the center.
     * 
     * @param centerToCamera
     */
    public final void setCenterToCamera(final Transform3d centerToCamera) {
        this.centerToCamera = centerToCamera;
    }

    /**
     * Get the current result.
     * @return The current result.
     */
    public final PhotonPipelineResult getResult() { return result; }

    /**
     * Get the current target.
     * @return The current target.
     */
    public final PhotonTrackedTarget getTarget() { return target; }

    /**
     * Does the camera have any targets?
     *
     * @return If we have targets or not.
     */
    public final boolean hasTargets() { return result.hasTargets(); }

    /**
     * How many targets do we see?
     * 
     * @return The number of targets that the camera detects.
     */
    public final int getNumberOfTargets() { return result.getTargets().size(); }

    /**
     * An estimate of the robot's position as a Pose3d based on a map of known April Tags
     * and the camera stream.
     * 
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose3d.
     */
    public final Optional<Pose3d> getRobotPoseAprilTag3d(final Map<Integer, Pose3d> knownTags) {
        return getRobotPoseAprilTag3d(knownTags, Double.MIN_VALUE, Double.MAX_VALUE);
    }

  /**
     * An estimate of the robot's position as a Pose3d based on a map of known April Tags
     * and the camera stream.
     * 
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose3d.
     */
    public final Optional<Pose3d> getRobotPoseAprilTag3d(final Map<Integer, Pose3d> knownTags, final double minDistance, final double maxDistance) {
        final Pose3d aprilTagLocation = knownTags.getOrDefault(target.getFiducialId(), null);
        if (aprilTagLocation == null) return Optional.empty();
        final Optional<Transform3d> transformWrapper = getTransformToAprilTag3d();
        if (transformWrapper.isEmpty()) return Optional.empty();
        final Transform3d transform = transformWrapper.get();
        final double distance = Math.sqrt(transform.getX() * transform.getX() 
                + transform.getY() * transform.getY()
                + transform.getZ() * transform.getZ());
        if (distance > maxDistance || distance < minDistance)
            return Optional.empty();
        final Pose3d robotLocation = aprilTagLocation.transformBy(transform);
        return Optional.of(robotLocation);
    }

    /**
     * An estimate of the robot's position as a Pose2d based on a map of known April Tags
     * and the camera stream.
     * 
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose2d.
     */
    public final Optional<Pose2d> getRobotPoseAprilTag2d(final Map<Integer, Pose3d> knownTags) {
        final Optional<Pose3d> pose3d = getRobotPoseAprilTag3d(knownTags);
        if (pose3d.isEmpty()) return Optional.empty();
        return Optional.of(pose3d.get().toPose2d());
    }

    /**
     * The estimated transformation between the camera and the identified AprilTag as a Transform3d.
     * 
     * @return The estimated transformation as a Transform3d.
     */
    public final Optional<Transform3d> getTransformToAprilTag3d() {
        if (target == null) return Optional.empty();
        final Transform3d transform = target.getBestCameraToTarget().inverse(); 
        final Transform3d adjusted = transform.plus(centerToCamera);
        return Optional.of(adjusted);
    }

    /**
     * The estimated transformation between the camera and the identified AprilTag as a Transform2d.
     * 
     * @return The estimated transformation as a Transform2d.
     */
    public final Optional<Transform2d> getTransformToKnownTag2d() {
        final Optional<Transform3d> transform3d = getTransformToAprilTag3d();
        if (transform3d.isEmpty()) return Optional.empty();
        final Transform2d transform2d = transform3dTo2d(transform3d.get());
        return Optional.of(transform2d);
    }

    /**
     * Converts a Transform3d to a Transform2d.
     * 
     * @return The converted Transform2d. 
     */
    public static final Transform2d transform3dTo2d(final Transform3d transform) {
        final Pose2d pose = (new Pose3d(transform)).toPose2d();
        return new Transform2d(pose.getTranslation(), pose.getRotation());
    }

    /**
     * Reads the camera latency in milliseconds.
     *
     * @return The camera latency in milliseconds
     */
    public final double getLatency() { return result.getLatencyMillis(); }
}
