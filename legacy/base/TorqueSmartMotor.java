/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.legacy.base;

/**
 * Collective interface for PID and encoder motors: "smart motors".
 *
 * May be removed if it proves no use.
 *
 * @author Justus Languell
 */
public interface TorqueSmartMotor extends TorquePIDMotor, TorqueEncoderMotor {}
