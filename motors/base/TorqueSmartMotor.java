package org.texastorque.torquelib.motors.base;

/**
 * Collective interface for PID and encoder motors: "smart motors".
 *
 * May be removed if it proves no use.
 *
 * @author Justus Languell
 */
public interface TorqueSmartMotor extends TorquePIDMotor, TorqueEncoderMotor {}
