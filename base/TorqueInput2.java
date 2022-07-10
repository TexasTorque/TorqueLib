/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.base;

import org.texastorque.torquelib.sensors.TorqueController;

/**
 * Input base class. Holds references to controllers.
 * 
 * @author Justus Languell
 */
public abstract class TorqueInput2 {
    protected TorqueController driver, operator;

    public abstract void update();
}
