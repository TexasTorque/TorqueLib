/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;
import org.texastorque.torquelib.motors.TorqueNEO;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

import org.texastorque.Input;

/**
 *
 * @author Tima Gezalov
 */
public class TorqueDebug {

    public static ArrayList<TorqueDebug> debugs = new ArrayList<TorqueDebug>();
    private final TorqueNEO motor;
    private final String name;

    public TorqueDebug(final TorqueNEO motor, final String name) {
        debugs.add(this);
        this.motor = motor;
        this.name = name;
    }

    public void update() {
        if(!Input.getInstance().isDebugMode()) {
            SmartDashboard.putNumber(name + " debug", 0);
        } else {
            motor.setVolts(SmartDashboard.getNumber(name + " debug", 0));
        }
    }
}