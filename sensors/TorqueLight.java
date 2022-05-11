package org.texastorque.torquelib.sensors;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

public class TorqueLight {
    private final PhotonCamera cam;

    private PhotonPipelineResult result;
    private PhotonTrackedTarget target;

    private final double cameraHeight, targetHeight;
    private final Rotation2d angle;

    public TorqueLight(final double cameraHeight, final double targetHeight, final Rotation2d angle) {
        // this.cam = new PhotonCamera(NetworkTableInstance.getDefault(),"torquecam");
        this.cam = null;
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();

        this.cameraHeight = cameraHeight;
        this.targetHeight = targetHeight;
        this.angle = angle;
    }

    public final void update() {
        result = cam.getLatestResult();
        if (result.hasTargets()) target = result.getBestTarget();
    }

    public final boolean hasTargets() { return result.hasTargets(); }

    public final double getTargetArea() { return target.getArea(); }

    public final double getTargetYaw() { return target.getYaw(); }

    public final double getTargetPitch() { return target.getPitch(); }

    public final Transform2d getCameraToTarget() { return target.getCameraToTarget(); }

    public final double getDistance() {
        return PhotonUtils.calculateDistanceToTargetMeters(cameraHeight, targetHeight, angle.getRadians(),
                                                           Units.degreesToRadians(getTargetPitch()));
    }
}
