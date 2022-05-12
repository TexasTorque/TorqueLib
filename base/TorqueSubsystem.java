package org.texastorque.torquelib.base;

/**
 * Subsystem template.
 * Extend this class for each subsystem class and override the methods.
 *
 * @author Justus
 */
public abstract class TorqueSubsystem {

    public abstract void initTeleop();
    public abstract void updateTeleop();

    public abstract void initAuto();
    public abstract void updateAuto();

    public void initDisabled() {}
    public void updateDisabled() {}

    public void smartDashboard() {}
}
