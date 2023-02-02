/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Improvments to the algorithm used to drive a West Coast Drivetrain
 * durring teleop. Encapsulated in a class.
 *
 * @author ß
 * @author Justus Languell
 * @author Jacob Daniels
 */
public final class TorqueWestCoast {
    private final double power = 2;
    private double forward = 3, turn = .2, coef = 1, left = 0, right = 0;

    public TorqueWestCoast(final double forward, final double turn) {
        this.forward = forward;
        this.turn = turn;
    }

    @Deprecated
    public final void setCoef(final double coef) {
        this.coef = coef;
    }

    public final void calculate(final double speed, final double rotation) {
        left = -coef * (-forward * speed - turn * Math.pow(rotation, power) * Math.signum(rotation));
        right = coef * (forward * speed - turn * Math.pow(rotation, power) * Math.signum(rotation));
    }

    public final double getLeft() { return left; }

    public final double getRight() { return right; }

    public final void setForwardCoef(final double forward) { this.forward = forward; }

    public final void setTurnCoef(final double turn) { this.turn = turn; }

    public final void setLeft(double left) { this.left = left; }

    public final void setRight(double right) { this.right = right; }
}