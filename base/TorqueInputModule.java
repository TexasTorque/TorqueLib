package org.texastorque.torquelib.base;

/**
 * @deprecated please use {@link org.texastorque.torquelib.input.TorqueInput}
 */
public interface TorqueInputModule {
    public void update();

    public void reset();

    public void smartDashboard();
}
