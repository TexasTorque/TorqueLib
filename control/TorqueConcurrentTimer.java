package org.texastorque.torquelib.control;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueConcurrentTimer {

    public TorqueConcurrentTimer() {
        double t = Timer.getFPGATimestamp();
        System.out.println(t);
    }

    public void update() {
        double t = Timer.getFPGATimestamp();
        System.out.println(t);
    }
}
