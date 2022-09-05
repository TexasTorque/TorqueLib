/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.motors.base;

/**
 * Collective interface for PID and encoder motors: "smart motors".
 *
 * May be removed if it proves no use.
 *
 * @author Justus Languell
 */
public interface TorqueSmartMotor extends TorquePIDMotor, TorqueEncoderMotor {}
