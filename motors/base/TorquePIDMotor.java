package org.texastorque.torquelib.motors.base;

import org.texastorque.torquelib.util.KPID;

/**
 * Interface to include PID methods for a PID motor.
 */
public interface TorquePIDMotor {

    public void configurePID(KPID kPID);

    public void updatePID(KPID kPID);
}
