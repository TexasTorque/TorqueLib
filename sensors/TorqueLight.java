package org.texastorque.torquelib.sensors;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
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

    private final double cameraHeight, targetHeight;
    private final Rotation2d angle;

    /**
     * Creates a new TorqueLight object with desired physical parameters.
     * 
     * @param cameraHeight The height of the camera in meters.
     * @param targetHeight The height of the target in meters.
     * @param angle        The angle of the camera as a Rotation2d.
     */
    public TorqueLight(final double cameraHeight, final double targetHeight, final Rotation2d angle) {
        this.cam = new PhotonCamera(NetworkTableInstance.getDefault(),"torquecam");
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();

        this.cameraHeight = cameraHeight;
        this.targetHeight = targetHeight;
        this.angle = angle;
    }

    /**
     * Call this periodically to update the vision state.
     */
    public final void update() {
        result = cam.getLatestResult();
        if (result.hasTargets()) 
            target = result.getBestTarget();
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
    public final double getDistance() {
        return PhotonUtils.calculateDistanceToTargetMeters(cameraHeight, targetHeight, angle.getRadians(),
                                                           Units.degreesToRadians(getTargetPitch()));
    }
}
