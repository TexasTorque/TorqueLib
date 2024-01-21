package org.texastorque.torquelib.control;

import java.util.TreeMap;
import java.util.Map.Entry;

public class TorqueLookUpTable {

    public TreeMap<Double, ShotParameter> table;

    public TorqueLookUpTable(TreeMap<Double, ShotParameter> table) {
        this.table = table;
    }

    public ShotParameter get(double distanceToTarget) {
        Entry<Double, ShotParameter> ceil = table.ceilingEntry(distanceToTarget);
        Entry<Double, ShotParameter> floor = table.floorEntry(distanceToTarget);
        if (ceil == null) return floor.getValue();
        if (floor == null) return ceil.getValue();
        if (ceil.getValue().equals(floor.getValue())) return ceil.getValue();
        return floor.getValue().interpolate(
            ceil.getValue(), 
            (distanceToTarget - floor.getKey()) / (ceil.getKey() - floor.getKey())
        );
    }

    public static class ShotParameter {
        public final double hoodAngle;
        public final double flywheelRPM;

        public ShotParameter( double flywheelRPM, double hoodAngle) {
            this.flywheelRPM = flywheelRPM;
            this.hoodAngle = hoodAngle;
        }

        public boolean equals(ShotParameter other) {
            return Math.abs(other.hoodAngle - hoodAngle) < 0.1 &&
                    Math.abs(other.flywheelRPM - flywheelRPM) < 0.1;
        }

        public ShotParameter interpolate(ShotParameter end, double t) {
            return new ShotParameter(
                    lerp(hoodAngle, end.hoodAngle, t),
                    lerp(flywheelRPM, end.flywheelRPM, t));
        }

        private double lerp(double y1, double y2, double t) {
            return y1 + (t * (y2 - y1));
        }
    }
}
