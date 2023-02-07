/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

/**
 * @apiNote This is a big bruh moment...
 *          You overload the update method
 *          You run it using the run method
 *          (this is for the assist sequence)
 *
 * @author Justus (see me for questions)
 *
 * @deprecated No longer used in new project structure.
 */
@Deprecated(forRemoval = true)
public abstract class TorqueInputModule {
    private boolean blocked = false;

    public final void block() { blocked = true; }

    public final void unblock() { blocked = false; }

    public final boolean isBlocked() { return blocked; }

    public abstract void update();

    public void smartDashboard(){};

    public final void run() {
        if (!blocked) update();
        unblock();
    };

    protected void reset(){}
}
