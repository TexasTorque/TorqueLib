package org.texastorque.torquelib.auto.sequences;

import org.texastorque.torquelib.auto.TorqueSequence;

/**
 * An empty TorqueSequence for use as default
 * by TorqueAutoManager.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Justus
 */
public final class TorqueEmpty extends TorqueSequence {

    public TorqueEmpty(String name) { super(name); }

    @Override
    protected void init() {}
}