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
        this.cam = new PhotonCamera(NetworkTableInstance.getDefault(),"torquecam");
        this.result = new PhotonPipelineResult();
        this.target = new PhotonTrackedTarget();

        this.cameraHeight = cameraHeight;
        this.targetHeight = targetHeight;
        this.angle = angle;
    }

    public void update() {
        result = cam.getLatestResult();
        if (result.hasTargets())
            target = result.getBestTarget();
    }

    public boolean hasTargets() {
        return result.hasTargets();
    }

    public double getTargetArea() {
        return target.getArea();
    }

    public double getTargetYaw() {
        return target.getYaw();
    }

    public double getTargetPitch() {
        return target.getPitch();
    }

    public Transform2d getCameraToTarget() {
        return target.getCameraToTarget();
    }

    public double getDistance() {
        return PhotonUtils.calculateDistanceToTargetMeters(
                cameraHeight, targetHeight, angle.getRadians(), 
                Units.degreesToRadians(getTargetPitch())
        );
    }
   
}
