package org.texastorque.torquelib.sensors;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;

/**
 * An extended wrapper class for the NavX gyro.
 * 
 * Also represents the NavX as a singleton object.
 * 
 * @author Justus Languell
 * @author Jack Pittenger
 */
public class TorqueNavXGyro extends AHRS {
    private static volatile TorqueNavXGyro instance; 

    private double angleOffset = 0;

    private TorqueNavXGyro() {
        super(SPI.Port.kMXP);
        getFusedHeading();
    } 

    public void setAngleOffset(final double angleOffset) {
        this.angleOffset = (angleOffset - getFusedHeading() + 360) % 360;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public double getDegreesClockwise() {
        return (getFusedHeading() + angleOffset) % 360;
    }

    public float getDegreesCounterClockwise() {
        return 360 - getFusedHeading();
    }

    @Override
    public Rotation2d getRotation2d() {
        return getRotation2dClockwise();
    }

    public Rotation2d getRotation2dClockwise() {
        return Rotation2d.fromDegrees(getDegreesClockwise());
    }

    public Rotation2d getRotation2dCounterClockwise() {
        return Rotation2d.fromDegrees(getDegreesCounterClockwise());
    }

    public static synchronized TorqueNavXGyro getInstance() {
        return instance == null ? instance = new TorqueNavXGyro() : instance;
    }
}
