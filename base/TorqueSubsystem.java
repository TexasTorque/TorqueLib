package org.texastorque.torquelib.base;

/**
 * Subsystem template.
 * Extend this class for each subsystem class and override the methods.
 */
public abstract class TorqueSubsystem {
    public void initTeleop() {
    }

    public void updateTeleop() {
    }

    public void initAuto() {
    }

    public void updateAuto() {
    }

    public void initDisabled() {
    }

    public void updateDisabled() {
    }

    public abstract void output();

    public void updateFeedback() {
    }

    @Deprecated
    public void updateFeedbackTeleop() {
    }

    @Deprecated
    public void updateFeedbackAuto() {
    }

    public void updateSmartDashboard() {
    }

}
