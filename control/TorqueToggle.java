package org.texastorque.torquelib.control;

/**
 * A containerized boolean wrapper that uses TorqueClick logic.
 * 
 * @author Justus Languell
 */
public final class TorqueToggle {
    private final TorqueClick click;
    private boolean value;

    public TorqueToggle(final boolean value) {
        this.click = new TorqueClick(value);
        this.value = value;
    }

    public final void set(final boolean value) {
        this.value = click.calculate(value);
    }

    public final boolean get() { return value; }
}
