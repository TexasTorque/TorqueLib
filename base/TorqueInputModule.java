package org.texastorque.torquelib.base;

/**
 * @deprecated please use {@link org.texastorque.torquelib.input.TorqueInput}
 */
@Deprecated 
interface TorqueInputModule {
    public void update();

    public void reset();

    public void smartDashboard();
}
