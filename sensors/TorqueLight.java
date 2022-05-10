package org.texastorque.torquelib.sensors;

import org.texastorque.torquelib.sensors.base.TorqueVisionCamera;

// import org.photonvision.PhotonCamera;
// import org.photonvision.PhotonUtils;
// import org.photonvision.targeting.PhotonPipelineResult;
// import org.photonvision.targeting.PhotonTrackedTarget;

public class TorqueLight implements TorqueVisionCamera {
    // private final PhotonCamera torqueCam = new PhotonCamera(NetworkTableInstance.getDefault(),
    // "torquecam");

    @Override
    public double getDistance() {
        return 0;
    }
}
