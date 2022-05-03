package org.texastorque.torquelib.util;

/**
 * A class that represents a PIDF controller with a min and max output.
 * 
 * @author Texas Torque
 * 
 * @apiNote Needs proper update / replacement.
 */
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

    public KPID(double pGains, double iGains, double dGains, double fGains, double minOutput, double maxOutput, double iZone) {
        this.pGains = pGains;
        this.iGains = iGains;
        this.dGains = dGains;
        this.fGains = fGains;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
        this.iZone = iZone;
    }

    public void setP(double pGains) {
        this.pGains = pGains;
    }

    public void setI(double iGains) {
        this.iGains = iGains;
    }

    public void setD(double dGains) {
        this.dGains = dGains;
    }

    public void setF(double fGains) {
        this.fGains = fGains;
    }

    public void setMin(double minOutput) {
        this.minOutput = minOutput;
    }

    public void setMax(double maxOutput) {
        this.maxOutput = maxOutput;
    }

    public void setIZone(double iZone) {
        this.iZone = iZone;
    }

    public double getPGains() {
        return pGains;
    }

    public double getIGains() {
        return iGains;
    }

    public double getDGains() {
        return dGains;
    }

    public double getFGains() {
        return fGains;
    }

    public double getMin() {
        return minOutput;
    }

    public double getMax() {
        return maxOutput;
    }

    public double getIZone() {
        return iZone;
    }
}