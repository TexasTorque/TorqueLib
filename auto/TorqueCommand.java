/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.auto;

/**
 * Texas Torque autonomous command base class.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Texas Torque
 */
public abstract class TorqueCommand {
    private boolean ended = false, started = false;

    public final boolean run() {
        if (ended) return ended;
        if (!started) {
            init();
            started = true;
        }
        continuous();
        if (endCondition()) {
            end();
            ended = true;
        }
        return ended;
    }

    public final void reset() {
        end();
        ended = false;
        started = false;
    }

    protected abstract void init();

    protected abstract void continuous();

    protected abstract boolean endCondition();

    protected abstract void end();
}