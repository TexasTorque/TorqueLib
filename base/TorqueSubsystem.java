/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.base;

import org.texastorque.torquelib.util.TorqueLog;


/**
 * Subsystem template.
 * Extend this class for each subsystem class and override the methods.
 *
 * @author Justus Languell
 */
public abstract class TorqueSubsystem {
    protected TorqueLog log; 

    @Deprecated
    public void initTeleop() {}
    // public abstract void initTeleop();
    @Deprecated
    public void updateTeleop() {}
    // public abstract void updateTeleop();

    @Deprecated
    public void initAuto() {}
    // public abstract void initAuto();
    @Deprecated
    public void updateAuto() {}
    // public abstract void updateAuto();

    @Deprecated
    public void initDisabled() {}
    @Deprecated
    public void updateDisabled() {}

    public abstract void initialize(final TorqueMode mode);

    public abstract void update(final TorqueMode mode);

    public void log() {}
}
