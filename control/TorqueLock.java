/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

/**
 * Lock the state of a value.
 *
 * @author Jack Pittenger
 * @author Justus Languell
 */
public final class TorqueLock<T> {
    private boolean locked;
    private T cached;

    public TorqueLock() { locked = false; }

    /**
     * Returns the requested value if not locked, otherwise
     * returns the cached last requested value, the value
     * it was locked at.
     */
    public final T calculate(final T requested) { return locked ? cached : (cached = requested); }

    public final void setLocked(final boolean locked) { this.locked = locked; }
    public final boolean isLocked() { return locked; }
    public final void lock() { setLocked(true); }
    public final void unlock() { setLocked(false); }

    public T getCached() { return cached; }
}
