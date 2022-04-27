package org.texastorque.torquelib.auto;

/**
 * An empty TorqueSequence for use as default
 * by TorqueAutoManager.
 * 
 * Part of the Texas Torque Autonomous Framework.
 * 
 * @author Justus
 */
public class TorqueEmpty extends TorqueSequence {

    public TorqueEmpty(String name) {
        super(name);
    }

    @Override
    protected void init() {

    }

}