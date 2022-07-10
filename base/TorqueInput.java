/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.base;

import org.texastorque.torquelib.util.GenericController;

/**
 * Input base class. Holds references to controllers.
 *
 * @author Justus Languell
 * 
 * @deprecated Holds references to {@link GenericController}, which is deprecated.
 * 
 * @apiNote Use {@link TorqueInput2} until this class is updated.
 */
public abstract class TorqueInput {
    protected GenericController driver, operator;

    public abstract void update();

    public void smartDashboard() {}
}
