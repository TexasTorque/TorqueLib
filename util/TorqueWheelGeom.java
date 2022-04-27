package org.texastorque.torquelib.util;

import org.texastorque.torquelib.component.TorqueSparkMax;

/**
 * Simple utility class for wheel geometry and conversions.
 */
public class TorqueWheelGeom {
    private double diameter;
    private double gearRatio;

    public TorqueWheelGeom(double diameter, double gearRatio) {
        this.diameter = diameter;
        this.gearRatio = gearRatio;
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
        return 2048. * meters * getCircumference() * gearRatio;
    }

    public double rotationsToMeters(double rotations) {
        return rotations / (2048. * getCircumference() * gearRatio);
    }

    /**
    Finds Velocity of a Drive Wheel in RPMs
    
    @param motor TorqueSparkMax motor
    @param gearRatio Gear Ratio
    @return RPM of the Wheel
    */
    public static double getWheelRPM(TorqueSparkMax motor, double gearRatio) {
        return motor.getVelocity() / gearRatio;
    }
    
    /**
    Finds Velocity of a Drive Wheel in meters per second
        *
    @param motor TorqueSparkMax motor
    @param gearRatio Gear Ratio
    @param wheelRadiusMeters Radius of Wheel in Meters
    @return Velocity of Wheel in m/s
    */
    public static double getWheelVelocity(TorqueSparkMax motor, double gearRatio, double wheelRadiusMeters) {
        return getWheelRPM(motor, gearRatio) * 2 * Math.PI * wheelRadiusMeters / 60;
    }










}
