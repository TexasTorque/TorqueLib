package org.texastorque.torquelib.util;

public interface Loggable {

    /**
     * Get names of logged items.
     *
     * @return The names of values logged by this system.
     */
    public abstract String getLogNames();

    /**
     * Get values of logged items.
     *
     * @return The values of items logged by this system.
     */
    public abstract String getLogValues();
}
