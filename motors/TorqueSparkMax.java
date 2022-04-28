package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.texastorque.torquelib.motors.base.TorqueEncoderMotor;
import org.texastorque.torquelib.motors.base.TorqueMotor;
import org.texastorque.torquelib.motors.base.TorquePIDMotor;
import org.texastorque.torquelib.util.KPID;

public class TorqueSparkMax extends TorqueMotor implements TorquePIDMotor, TorqueEncoderMotor {
    private CANSparkMax sparkMax;
    private RelativeEncoder sparkMaxEncoder;
    private SparkMaxAlternateEncoder alternateEncoder;
    private SparkMaxPIDController pidController;
    private SparkMaxAnalogSensor analogEncoder;
    private ArrayList<CANSparkMax> sparkMaxFollowers = new ArrayList<>();

    private final double CLICKS_PER_ROTATION = sparkMaxEncoder.getCountsPerRevolution();

    private double lastVelocity;
    private long lastVelocityTime;

    private double encoderZero = 0;

    public TorqueSparkMax(final int port) {
        super(port);

        this.lastVelocity = 0;
        this.lastVelocityTime = System.currentTimeMillis();
        sparkMax = new CANSparkMax(port, MotorType.kBrushless);
        sparkMaxEncoder = sparkMax.getEncoder();
        analogEncoder = sparkMax.getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
        pidController = sparkMax.getPIDController();
    }

    @Override
    public void addFollower(final int port) {
        sparkMaxFollowers.add(new CANSparkMax(port, MotorType.kBrushless));
    }

    @Override
    public void configurePID(final KPID kPID) {
        pidController.setP(kPID.getPGains());
        pidController.setI(kPID.getIGains());
        pidController.setD(kPID.getDGains());
        pidController.setFF(kPID.getFGains());
        double iZone;
        if ((iZone = kPID.getIZone()) > 0)
            pidController.setIZone(iZone);
        pidController.setIZone(kPID.getIGains());
        pidController.setOutputRange(kPID.getMin(), kPID.getMax());
    }

    @Override
    public void setPercent(final double percent) {
        sparkMax.set(percent);
        for (CANSparkMax canSparkMax : sparkMaxFollowers)
            canSparkMax.follow(sparkMax);
    }

    // Setters implemented from TorquePIDMotor

    @Override
    public void setPosition(final double setpoint) {
        setPositionRotations(setpoint / CLICKS_PER_ROTATION);
    }

    @Override
    public void setPositionDegrees(final double setpoint) {
        setPositionRotations(setpoint / 360);
    }

    @Override
    public void setPositionRotations(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kPosition);
            for (CANSparkMax follower : sparkMaxFollowers)
                follower.follow(sparkMax);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID", port);
        }
    }

    @Override
    public void setVelocity(final double setpoint) {
        setPositionRotations(setpoint / CLICKS_PER_ROTATION); 
    }

    @Override
    public void setVelocityRPS(final double setpoint) {
        setPositionRotations(setpoint / 360); 
    }

    @Override
    public void setVelocityRPM(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kVelocity);
            for (CANSparkMax follower : sparkMaxFollowers)
                follower.follow(sparkMax);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID", port);
        } 
    }

    // Getters implemented from TorqueEncoderMotor

    @Override
    public double getPosition() {
        return getPosition() * CLICKS_PER_ROTATION;
    }

    @Override
    public double getPositionDegrees() {
        return getPosition() * 360;
    }

    @Override
    public double getPositionRotations() {
        return sparkMaxEncoder.getPosition() - encoderZero;
    }

    @Override
    public double getVelocity() {
        return getVelocityRPS() * CLICKS_PER_ROTATION;
    }

    @Override
    public double getVelocityRPS() {
        return getVelocityRPM() / 60;
    }

    @Override
    public double getVelocityRPM() {
        return sparkMaxEncoder.getVelocity();
    }

    @Override
    public double getAcceleration() {
        return getAccelerationRPM() * CLICKS_PER_ROTATION;
    }

    @Override
    public double getAccelerationRPS() {
        return getAccelerationRPM() / 60; 
    }

    @Override
    public double getAccelerationRPM() {
        final double currentVelocity = getVelocityRPM();
        final long currentTime = System.currentTimeMillis();

        final double acceleration = (currentVelocity - lastVelocity) / (currentTime - lastVelocityTime);

        lastVelocity = currentVelocity;
        lastVelocityTime = currentTime;

        return acceleration;
    }


  

    


}
