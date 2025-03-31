/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.commands;

import java.util.function.BooleanSupplier;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.marker.Marker;

import edu.wpi.first.wpilibj.Timer;

public final class TorqueWaitTimeUntil extends TorqueCommand {
    private final BooleanSupplier condition;
	private Timer timer;
	private double time;
	private Marker[] markers;

    public TorqueWaitTimeUntil(final double time, final BooleanSupplier condition, final Marker ...markers) {
		this.time = time;
        this.condition = condition;
		this.timer = new Timer();
		this.markers = markers;
		if (this.markers == null) this.markers = new Marker[0];
    }

    @Override
    protected final void init() {
		timer.restart();
    }

    @Override
    protected final void continuous() {
        for (Marker marker : markers) {
			if (!marker.hasRan() && timer.hasElapsed(time * marker.getRelativePosition())) {
				marker.run();
			}
		}
    }

    @Override
    protected final boolean endCondition() {
        return timer.hasElapsed(time) || condition.getAsBoolean();
    }

    @Override
    protected final void end() {
        
    }
}