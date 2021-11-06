package org.texastorque.torquelib.util;

/**
 * Simple utility class for wheel geometry and conversions.
 */
public class TorqueWheelGeom {
    private double diameter;

    public TorqueWheelGeom(double diameter) {
        this.diameter = diameter;
    }

    public double getDiameter() {
        return diameter;
    }

    public double setDiameter(double diameter) {
        return this.diameter = diameter;
    }

    public double getRadius() {
        return diameter / 2.0;
    }

    public double setRadius(double radius) {
        return diameter = 2.0 * radius;
    }

    public double getCircumference() {
        return Math.PI * diameter;
    }

    public double metersToRotations(double meters) {
        return 4. * (30. * meters) / (Math.PI * diameter / 2.);
    }

    public double rotationsToMeters(double rotations) {
        return 2. * Math.PI * diameter / 2. * rotations / 4. / 60.;
    }
}
