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
        DisjointData lowestPoint = baseData.floor(cache);
        DisjointData highestPoint = baseData.ceiling(cache);

        if (lowestPoint == null)
            return highestPoint;
        else if (highestPoint == null)
            return lowestPoint;

        if (Math.abs(lowestPoint.getDistance() - distance) < .2) { // If the data point is within .3m, then theres no need to interpolate
            return lowestPoint;
        } else if (Math.abs(highestPoint.getDistance() - distance) < .2){
            return highestPoint;
        } else return getLerpPoint(lowestPoint, highestPoint, distance);

    }

    /**
        Returns a calculated data point based off of a linear fit between the two closest disjoint data points
    */
    public DisjointData getLinearPoint(TorqueDisjointDataRegression.DisjointData lowestPoint,
            TorqueDisjointDataRegression.DisjointData highestPoint, double distance) {
        double deltaDistance = highestPoint.getDistance() - lowestPoint.getDistance();

        double slopeHood = (highestPoint.getHood() - lowestPoint.getHood()) / deltaDistance;
        double slopeRPM = (highestPoint.getRPM() - lowestPoint.getRPM()) / deltaDistance;
        double hoodYInt = 0, rpmYInt = 0;

        if (slopeRPM * lowestPoint.getDistance() != lowestPoint.getRPM())
            rpmYInt = lowestPoint.getRPM() - (slopeRPM * lowestPoint.getDistance());
        if (slopeHood * lowestPoint.getDistance() != lowestPoint.getHood())
            hoodYInt = lowestPoint.getHood() - (slopeHood * lowestPoint.getDistance());

        DisjointData linearData = new DisjointData(lowestPoint.getDistance(), highestPoint.getDistance(),
                (distance * slopeHood + hoodYInt), (distance * slopeRPM + rpmYInt));
        return linearData;
    }

     /**
        Returns a calculated data point based off of a lerp fit between the two closest disjoint data points
    */
    public DisjointData getLerpPoint(TorqueDisjointDataRegression.DisjointData lowestPoint,
            TorqueDisjointDataRegression.DisjointData highestPoint, double distance) {
        double hoodLerp = lowestPoint.getHood() + (highestPoint.getHood() - lowestPoint.getHood()) * 1. / 3.;
        double rpmLerp = lowestPoint.getRPM() + (highestPoint.getRPM() - lowestPoint.getRPM()) * 1. / 3.;
        DisjointData lerpData = new DisjointData(lowestPoint.getDistance(), highestPoint.getDistance(), hoodLerp,
                rpmLerp);
        return lerpData;
    }
    
    public class DisjointData {
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
        public DisjointData(double distance, double hood, double rpm) {
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
        public DisjointData(double lowestPoint, double highestPoint, double hood, double rpm) {
            this.lowestPoint = lowestPoint;
            this.highestPoint = highestPoint;
            this.hood = hood;
            this.rpm = rpm;
        }

        public double getHood() {
            return hood;
        }

        public double getRPM() {
            return rpm;
        }

        public double getDistance() {
            return distance;
        }

        public double getLowestPoint() {
            return lowestPoint;
        }

        public double getHighestPoint() {
            return highestPoint;
        }
    }

    class DisjointDataComparator implements Comparator<DisjointData> {

        @Override
        public int compare(DisjointData o1, DisjointData o2) {
            return o1.distance > o2.distance ? 1 : o1.distance < o2.distance ? -1 : 0;
        }
    }

}