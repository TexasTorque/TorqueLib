/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import edu.wpi.first.wpilibj.Timer;
import org.texastorque.torquelib.auto.TorqueCommand;

public final class TorqueWait extends TorqueCommand {
    private double time, start;

    public TorqueWait(final double time) { this.time = time; }

    @Override
    protected final void init() {
        start = Timer.getFPGATimestamp();
    }

    @Override
    protected final void continuous() {}

    @Override
    protected final boolean endCondition() {
        return (Timer.getFPGATimestamp() - start) > time;
    }

    @Override
    protected final void end() {}
}