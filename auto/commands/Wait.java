package org.texastorque.torquelib.auto.commands;

import edu.wpi.first.wpilibj.Timer;
import org.texastorque.torquelib.auto.TorqueCommand;

public final class Wait extends TorqueCommand {
    private double time, start;

    public Wait(final double time) { this.time = time; }

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