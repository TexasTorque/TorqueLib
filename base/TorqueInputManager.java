package org.texastorque.torquelib.base;

import org.texastorque.torquelib.util.GenericController;

/**
 */
public abstract class TorqueInputManager {
    protected GenericController driver, operator;

    public abstract void update();

    public void smartDashboard() {}
}
