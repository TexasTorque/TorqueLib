/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.control;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Implements a lookup table with lerp interpolation between values
 * @author Jack Pittenger, Omar Afzal
 */
public class TorqueLookUpTable {

    private TreeSet<TorqueDisjointData> baseData;

    public TorqueLookUpTable() { baseData = new TreeSet<TorqueDisjointData>(new TorqueDisjointDataComparator()); }

    public void addDisjointData(TorqueDisjointData data) { baseData.add(data); }

    TorqueDisjointData cache = new TorqueDisjointData(0, 0, 0);

    /**
        Returns the closest disjoint data based on the distance provided.
    */
    public TorqueDisjointData calculate(double distance) {
        cache.distance = distance;
        TorqueDisjointData lowestPoint = baseData.floor(cache);
        TorqueDisjointData highestPoint = baseData.ceiling(cache);

        if (lowestPoint == null)
            return highestPoint;
        else if (highestPoint == null)
            return lowestPoint;

        if (Math.abs(lowestPoint.getDistance() - distance) <=
            .15) { // If the data point is within .3m, then theres no need to interpolate
            return lowestPoint;
        } else if (Math.abs(highestPoint.getDistance() - distance) <= .15) {
            return highestPoint;
        } else
            return getLerpPoint(lowestPoint, highestPoint, distance);
    }

    /**
        Returns a calculated data point based off of a linear fit between the two closest disjoint data points
    */
    public TorqueDisjointData getLinearPoint(TorqueLookUpTable.TorqueDisjointData lowestPoint,
                                             TorqueLookUpTable.TorqueDisjointData highestPoint, double distance) {
        double deltaDistance = highestPoint.getDistance() - lowestPoint.getDistance();

        double slopeHood = (highestPoint.getHood() - lowestPoint.getHood()) / deltaDistance;
        double slopeRPM = (highestPoint.getRPM() - lowestPoint.getRPM()) / deltaDistance;
        double hoodYInt = 0, rpmYInt = 0;

        if (slopeRPM * lowestPoint.getDistance() != lowestPoint.getRPM())
            rpmYInt = lowestPoint.getRPM() - (slopeRPM * lowestPoint.getDistance());
        if (slopeHood * lowestPoint.getDistance() != lowestPoint.getHood())
            hoodYInt = lowestPoint.getHood() - (slopeHood * lowestPoint.getDistance());

        TorqueDisjointData linearData =
                new TorqueDisjointData(lowestPoint.getDistance(), highestPoint.getDistance(),
                                       (distance * slopeHood + hoodYInt), (distance * slopeRPM + rpmYInt));
        return linearData;
    }

    /**
       Returns a calculated data point based off of a lerp fit between the two closest disjoint data points
    */
    public TorqueDisjointData getLerpPoint(TorqueLookUpTable.TorqueDisjointData lowestPoint,
                                           TorqueLookUpTable.TorqueDisjointData highestPoint, double distance) {
        double hoodLerp = lowestPoint.getHood() + (highestPoint.getHood() - lowestPoint.getHood()) * 1. / 3.;
        double rpmLerp = lowestPoint.getRPM() + (highestPoint.getRPM() - lowestPoint.getRPM()) * 1. / 3.;
        TorqueDisjointData lerpData =
                new TorqueDisjointData(lowestPoint.getDistance(), highestPoint.getDistance(), hoodLerp, rpmLerp);
        return lerpData;
    }

    public class TorqueDisjointData {
        public double distance;
        private double lowestPoint;
        private double highestPoint;
        private double hood;
        private double rpm;

        /**
         *
         * @param distance
         * @param hood
         * @param rpm
         */
        public TorqueDisjointData(double distance, double hood, double rpm) {
            this.distance = distance;
            this.hood = hood;
            this.rpm = rpm;
        }

        /**
         *
         * @param lowestPoint
         * @param highestPoint
         * @param hood
         * @param rpm
         */
        public TorqueDisjointData(double lowestPoint, double highestPoint, double hood, double rpm) {
            this.lowestPoint = lowestPoint;
            this.highestPoint = highestPoint;
            this.hood = hood;
            this.rpm = rpm;
        }

        public double getHood() { return hood; }

        public double getRPM() { return rpm; }

        public double getDistance() { return distance; }

        public double getLowestPoint() { return lowestPoint; }

        public double getHighestPoint() { return highestPoint; }
    }

    class TorqueDisjointDataComparator implements Comparator<TorqueDisjointData> {

        @Override
        public int compare(TorqueDisjointData o1, TorqueDisjointData o2) {
            return o1.distance > o2.distance ? 1 : o1.distance < o2.distance ? -1 : 0;
        }
    }
}