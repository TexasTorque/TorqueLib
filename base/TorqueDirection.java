package org.texastorque.torquelib.base;

/**
 * An enum to represent a three state representation of 
 * a direction with a quick multiplier.
 * 
 * @author Justus
 */
public enum TorqueDirection {
    REVERSE, NEUTRAL, FORWARD; 

    public final double get() { return ordinal() - 1.; }
}
