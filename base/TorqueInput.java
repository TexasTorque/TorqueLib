/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.base;

/**
 * Input base class. Holds references to controllers.
 * 
 * @author Justus Languell
 */
public abstract class TorqueInput<T> {
    protected T driver, operator;

    public abstract void update();

    public final T getDriver() {
        return driver;
    }

    public final T getOperator() {
        return operator;
    }
}
