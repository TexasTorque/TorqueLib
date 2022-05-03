package org.texastorque.torquelib.log;

public abstract class SBEntry {
    protected final String name;
    protected final Object defaultValue;

    public SBEntry(final String name, final Object defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
}
