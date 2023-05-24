/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.sensors;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Client interface for a TorqueCamera.
 *
 * TorqueCamera is a vision camera that can process AprilTags Machine Learning object detection, and
 * more.
 * 
 * TorqueCamera is based off of groundwork created by Jack Pittenger and PhotonVision.
 *
 * TorqueCamera is the updated version for the 2023 offseason that was designed to specialize in
 * April Tag detection and improve on the TorqueVision.
 *
 * The TorqueCamera class is updated to specialize in AprilTags and to use the Optional class to
 * avoid null pointer exceptions.
 *
 * @author Omar Afzal
 * @author Justus Languell
 * @author Jack Pittenger
 */
public final class TorqueCamera {

    /**
     * Converts a Transform3d to a Transform2d.
     *
     * @return The converted Transform2d.
     */
    public static final Transform2d transform3dTo2d(final Transform3d transform) {
        final Pose2d pose =
                new Pose3d(transform.getTranslation(), transform.getRotation()).toPose2d();
        return new Transform2d(pose.getTranslation(), pose.getRotation());
    }

    private NetworkTable networkTable;
    private String cameraName;
    private boolean hasTags;

    private PhotonPipelineResult result;

    private PhotonTrackedTarget target;
    public final PhotonPoseEstimator photonPoseEstimator;

    private Transform3d centerToCamera;

    private int lastGoodAprilTagID = -1;

    /**
     * Creates a new TorqueLight object with desired table name.
     *
     * @param name The name of the camera.
     * @param centerToCamera The transformation from the center to the camera.
     */
    public TorqueCamera(final String name, final AprilTagFieldLayout layout,
            final Transform3d centerToCamera) {

        this.cameraName = name;
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();
        setCenterToCamera(centerToCamera);
        photonPoseEstimator =
                new PhotonPoseEstimator(layout, PoseStrategy.MULTI_TAG_PNP, cam, centerToCamera);
        photonPoseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);


        networkTable = NetworkTableInstance.getDefault().getTable("Vision").getSubTable(cameraName);
    }


    /**
     * Call this periodically to update the vision state.
     */
    public final void update() {
        hasTags = networkTable.getEntry("hasTags").getBoolean(false);
        if (hasTags) {

            // The TorqueCamera will send all the tags it sees in an array, with each entry being an
            // array containing the tag id, the tag's pose, and ambiguity.
            System.out.println(networkTable.getEntry("tags").getValue());
        }
        if (result.hasTargets())
            target = result.getBestTarget();
    }

    // /**
    //  * Is the camera connected?
    //  * 
    //  * @return Is the camera connected?
    //  */
    // public final boolean isCameraConnected() {
    //     return networkTable.addListener();
    // }

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
     * 
     * @return The current result.
     */
    public final PhotonPipelineResult getResult() {
        return result;
    }

    /**
     * Get the current target.
     * 
     * @return The current target.
     */
    public final PhotonTrackedTarget getTarget() {
        return target;
    }

    /**
     * Does the camera have any targets?
     *
     * @return If we have targets or not.
     */
    public final boolean hasTargets() {
        return result.hasTargets();
    }

    /**
     * How many targets do we see?
     *
     * @return The number of targets that the camera detects.
     */
    public final int getNumberOfTargets() {
        return result.getTargets().size();
    }

    /**
     * An estimate of the robot's position as a Pose3d based on a map of known April Tags and the
     * camera stream.
     *
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose3d.
     */
    public final Optional<Pose3d> getRobotPoseAprilTag3d(final Map<Integer, Pose3d> knownTags,
            final double poseAmbiguity) {
        return getRobotPoseAprilTag3d(knownTags, poseAmbiguity, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * An estimate of the robot's position as a Pose3d based on a map of known April Tags and the
     * camera stream.
     *
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose3d.
     */
    public final Optional<Pose3d> getRobotPoseAprilTag3d(final Map<Integer, Pose3d> knownTags,
            final double poseAmbiguity, final double minDistance, final double maxDistance) {
        final Optional<Pose3d> aprilTagLocation =
                getPositionOfBestAprilTag(knownTags, poseAmbiguity);
        if (aprilTagLocation.isEmpty())
            return Optional.empty();

        final Optional<Transform3d> transformWrapper = getTransformToAprilTag3d(poseAmbiguity);
        if (transformWrapper.isEmpty())
            return Optional.empty();

        final Transform3d transform = transformWrapper.get();

        final Pose3d robotLocation = aprilTagLocation.get().transformBy(transform);
        return Optional.of(robotLocation);
    }

    /**
     * The estimated transformation between the camera and the identified AprilTag as a Transform3d.
     *
     * @param poseAmbiguity The maximum pose ambiguity to return a result.
     * @return The estimated transformation as a Transform3d.
     */
    public final Optional<Transform3d> getTransformToAprilTag3d(final double poseAmbiguity) {
        if (target == null)
            return Optional.empty();
        final Transform3d transform = target.getBestCameraToTarget().inverse();
        final Transform3d adjusted = transform.plus(centerToCamera);
        if (!(target.getPoseAmbiguity() <= poseAmbiguity && target.getPoseAmbiguity() != -1
                && target.getFiducialId() >= 0))
            return Optional.empty();
        return Optional.of(adjusted);
    }

    /**
     * Get position of best April Tag.
     *
     * @param knownTags The map of known April Tags.
     * @return The position estimate of the robot as a Pose3d.
     */
    public final Optional<Pose3d> getPositionOfBestAprilTag(final Map<Integer, Pose3d> knownTags,
            final double poseAmbiguity) {
        if (!(target.getPoseAmbiguity() <= poseAmbiguity && target.getPoseAmbiguity() != -1
                && target.getFiducialId() >= 0)) {
            lastGoodAprilTagID = -1;
            return Optional.empty();
        }

        lastGoodAprilTagID = target.getFiducialId();
        final Pose3d aprilTagLocation = knownTags.getOrDefault(lastGoodAprilTagID, null);
        if (aprilTagLocation == null)
            return Optional.empty();
        return Optional.of(aprilTagLocation);
    }

    /**
     * Reads the camera latency in milliseconds.
     *
     * @return The camera latency in milliseconds
     */
    @Deprecated
    public final double getLatency() {
        return result.getLatencyMillis();
    }

    /**
     * Reads the result timestamp in seconds. More accruate than getLatency.
     *
     * @return The result timestamp in seconds.
     */
    public final double getTimestamp() {
        return result.getTimestampSeconds();
    }

    public int getLastGoodAprilTagID() {
        return lastGoodAprilTagID;
    }

  

    /**
     * Add vision measurements to the pose estimator.
     *
     * @param addVisionMeasurement The PoseEstimator::addVisionMeasurement method.
     */
    public final void updateVisionMeasurement(
            final BiConsumer<Pose2d, Double> addVisionMeasurement) {
        update();
        if (!(target.getPoseAmbiguity() <= 0.1 && target.getPoseAmbiguity() != -1
                && target.getFiducialId() >= 0))
            return;
        if (target.getBestCameraToTarget().getTranslation().getDistance(new Translation3d()) >= 3)
            return;
        final Optional<EstimatedRobotPose> optionalEstimatedPose = photonPoseEstimator.update();
        if (optionalEstimatedPose.isPresent()) {
            final EstimatedRobotPose estimatedPose = optionalEstimatedPose.get();
            addVisionMeasurement.accept(estimatedPose.estimatedPose.toPose2d(),
                    estimatedPose.timestampSeconds);
        }
    }

    /**
     * Sets the field layout for the pose estimator.
     */
    public final void setFieldLayout(final AprilTagFieldLayout layout) {
        photonPoseEstimator.setFieldTags(layout);
    }

    /**
     * @return The name of the camera.
     */
    public final String getCameraName() {
        return cameraName;
    }
}
