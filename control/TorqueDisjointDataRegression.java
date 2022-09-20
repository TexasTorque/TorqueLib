package org.texastorque.torquelib.control;

import java.util.Comparator;
import java.util.TreeSet;

/**
    @author Jack Pittenger, Omar Afzal
 */
public class TorqueDisjointDataRegression {

    private TreeSet<DisjointData> baseData;

    public TorqueDisjointDataRegression() {
        baseData = new TreeSet<DisjointData>(new DisjointDataComparator());
    }

    public void addDisjointData(DisjointData data) {
        baseData.add(data);
    }

    DisjointData cache = new DisjointData(0, 0, 0);

    /**
        Returns the closest disjoint data based on the distance provided. 
    */
    public DisjointData calculate(double distance) {
        cache.distance = distance;
        DisjointData d = baseData.floor(cache);
        if (d == null)
            return baseData.ceiling(cache);
        else
            return d;
    }

    public class DisjointData {
        public double distance;
        private double hood;
        private double rpm;

        /**
         * 
         * @param distance
         * @param hood
         * @param rpm
         */
        public DisjointData(double distance, double hood, double rpm) {
            this.distance = distance;
            this.hood = hood;
            this.rpm = rpm;
        }

        public double getHood() {
            return hood;
        }

        public double getRPM() {
            return rpm;
        }
    }

    class DisjointDataComparator implements Comparator<DisjointData> {

        @Override
        public int compare(DisjointData o1, DisjointData o2) {
            return o1.distance > o2.distance ? 1 : o1.distance < o2.distance ? -1 : 0;
        }
    }

}