/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.util;

/**
 * A class that represents a PIDF controller with a min and max output.
 *
 * @author Texas Torque
 *
 * @apiNote Needs proper update / replacement.
 *
 * @deprecated Use {@link org.texastorque.torquelib.control.TorquePID} instead.
 */
@Deprecated
public final class KPID {

    private double pGains, iGains, dGains, fGains, minOutput, maxOutput, iZone;

    public KPID() {
        pGains = 0;
        iGains = 0;
        dGains = 0;
        fGains = 0;
        minOutput = -1;
        maxOutput = 1;
        iZone = 0;
    }

    public KPID(double pGains, double iGains, double dGains, double fGains, double minOutput, double maxOutput) {
        this.pGains = pGains;
        this.iGains = iGains;
        this.dGains = dGains;
        this.fGains = fGains;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.iZone = 0;
    }

    public KPID(double pGains, double iGains, double dGains, double fGains, double minOutput, double maxOutput,
                double iZone) {
        this.pGains = pGains;
        this.iGains = iGains;
        this.dGains = dGains;
        this.fGains = fGains;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.iZone = iZone;
    }

    public final void setP(double pGains) { this.pGains = pGains; }

    public final void setI(double iGains) { this.iGains = iGains; }

    public final void setD(double dGains) { this.dGains = dGains; }

    public final void setF(double fGains) { this.fGains = fGains; }

    public final void setMin(double minOutput) { this.minOutput = minOutput; }

    public final void setMax(double maxOutput) { this.maxOutput = maxOutput; }

    public final void setIZone(double iZone) { this.iZone = iZone; }

    public final double getPGains() { return pGains; }

    public final double getIGains() { return iGains; }

    public final double getDGains() { return dGains; }

    public final double getFGains() { return fGains; }

    public final double getMin() { return minOutput; }

    public final double getMax() { return maxOutput; }

    public final double getIZone() { return iZone; }
}