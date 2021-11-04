package org.texastorque.torquelib.component;

import java.util.ArrayList;

import com.revrobotics.CANAnalog;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.texastorque.torquelib.component.TorqueMotor;
import org.texastorque.util.KPID;

/**
 * TorqueMotor using SparkMax for CAN motor management.
 */
public class TorqueSparkMax extends TorqueMotor {

    private CANSparkMax sparkMax;
    private CANEncoder sparkMaxEncoder;
    private CANEncoder alternateEncoder;
    private CANAnalog analogEncoder;
    private ArrayList<CANSparkMax> sparkMaxFollowers = new ArrayList<>();

    private double encoderZero = 0;

    // ===================== constructor stuff =====================
    public TorqueSparkMax(int port) {
        this.port = port;
        sparkMax = new CANSparkMax(port, MotorType.kBrushless);
        sparkMaxEncoder = sparkMax.getEncoder();
        analogEncoder = sparkMax.getAnalog(AnalogMode.kAbsolute);
    } // constructor

    @Override
    public void addFollower(int port) {
        sparkMaxFollowers.add(new CANSparkMax(port, MotorType.kBrushless));
        System.out.println("Added spark max follower");
    } // add follower

    // ===================== set methods ==========================

    @Override
    public void set(double output) {
        sparkMax.set(output);
        for (CANSparkMax canSparkMax : sparkMaxFollowers) {
            canSparkMax.follow(sparkMax);
        } // takes care of followers
    } // raw set method (-1 to 1)

    public void set(double output, ControlType ctrlType) {
        try {
            pid.setReference(output, ctrlType);
            for (CANSparkMax follower : sparkMaxFollowers) {
                follower.follow(sparkMax, invert);
            } // takes care of followers
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("You need to configure the PID");
        } // try catch
    } // set method for use with PID, position or velocity

    /**
     * UNSAFE
     */
    public void enableVoltageCompensation() {
        sparkMax.enableVoltageCompensation(2);
        for (CANSparkMax follower : sparkMaxFollowers) {
            follower.enableVoltageCompensation(2);
        }
    }

    /**
     * UNSAFE
     */
    public void disableVoltageCompensation() {
        sparkMax.disableVoltageCompensation();
        for (CANSparkMax follower : sparkMaxFollowers) {
            follower.disableVoltageCompensation();
        }

    }

    /**
     * Directly set voltage output. Be careful!
     */
    public void setVoltage(double volts) {
        sparkMax.setVoltage(volts);
    }

    // ===================== PID stuff ========================
    private CANPIDController pid;

    @Override
    public void configurePID(KPID kPID) {
        pid = sparkMax.getPIDController();
        pid.setP(kPID.p());
        pid.setI(kPID.i());
        pid.setD(kPID.d());
        pid.setFF(kPID.f());
        pid.setOutputRange(kPID.min(), kPID.max());
    } // configure PID

    @Override
    public void updatePID(KPID kPID) {
        pid.setP(kPID.p());
        pid.setI(kPID.i());
        pid.setD(kPID.d());
        pid.setFF(kPID.f());
        pid.setOutputRange(kPID.min(), kPID.max());
    } // update PID

    public void setPosFactor(double factor) {
        sparkMaxEncoder.setPositionConversionFactor(factor);
    }

    @Override
    public double getVelocity() {
        return sparkMaxEncoder.getVelocity() * sparkMaxEncoder.getVelocityConversionFactor();
    } // returns velocity of motor

    /**
     * Get the velocity of the motor in meters
     * 
     * @param radius The radius (in meters) of the drive wheel
     * @return The velocity of the motor
     */
    public double getVelocityMeters(double radius) {
        return (2 * Math.PI * radius * getVelocity() / 60.0) / 4.0;
    }

    public void tareEncoder() {
        encoderZero = sparkMaxEncoder.getPosition();
    }

    public double getZero() {
        return encoderZero;
    }

    public double getAnalogValue() {
        // return (sparkMax.getAnalog(AnalogMode.kRelative).getPosition());
        return analogEncoder.getPosition() * analogEncoder.getPositionConversionFactor();

    }

    @Override
    public double getPosition() {
        return ((sparkMaxEncoder.getPosition() - encoderZero));
    } // returns position of motor

    public double getPositionConverted() {
        return ((sparkMaxEncoder.getPosition() - encoderZero) * sparkMaxEncoder.getPositionConversionFactor());
    } // returns motor position but converted by some factor

    public double getDegrees() {
        return getPosition() / sparkMaxEncoder.getCountsPerRevolution() * 360.0;
    }

    public double getCurrent() {
        return sparkMax.getOutputCurrent();
    }

    public void setAlternateEncoder() {
        // No params deprecated, default to 0
        alternateEncoder = sparkMax.getAlternateEncoder(0);
    }

    public void setAlternateEncoder(int n) {
        alternateEncoder = sparkMax.getAlternateEncoder(n);
    }

    public double getAlternateVelocity() {
        return alternateEncoder.getVelocity();
    }

    public double getAlternatePosition() {
        return alternateEncoder.getPosition();
    }

}
