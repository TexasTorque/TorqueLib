package org.texastorque.torquelib.base;

/**
 * Subsystem template.
 * Extend this class for each subsystem class and override the methods.
 */
public abstract class TorqueSubsystem {
    public void initTeleop() {}
    public void updateTeleop() {}

    public void initAuto() {}
    public void updateAuto() {}

    public void output() {}

    public void updateFeedbackTeleop() {}
    public void updateFeedbackAuto() {}
    public void updateSmartDashboard() {}

    public void disable() {}
}
