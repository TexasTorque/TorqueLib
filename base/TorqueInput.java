package org.texastorque.torquelib.base;

import org.texastorque.torquelib.util.GenericController;

/**
 * Input base class. Holds references to controllers.
 *
 * @author Justus Languell
 */
public abstract class TorqueInput {
    protected GenericController driver, operator;

    public abstract void update();

    public void smartDashboard() {}
}
