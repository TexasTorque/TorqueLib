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

    public TorqueSparkMax(int port) {
        super(port);

        this.lastVelocity = 0;
        this.lastVelocityTime = System.currentTimeMillis();
        sparkMax = new CANSparkMax(port, MotorType.kBrushless);
        sparkMaxEncoder = sparkMax.getEncoder();
        analogEncoder = sparkMax.getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
        pidController = sparkMax.getPIDController();
    }

    @Override
    public void addFollower(int port) {
        sparkMaxFollowers.add(new CANSparkMax(port, MotorType.kBrushless));
    }

    @Override
    public void configurePID(KPID kPID) {
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
    public void setPercent(double percent) {
        sparkMax.set(percent);
        for (CANSparkMax canSparkMax : sparkMaxFollowers) {
            canSparkMax.follow(sparkMax);
        } 
    }

    @Override
    public double getPosition() {
        return sparkMaxEncoder.getPosition() - encoderZero;
    }

    @Override
    public double getPositionDegrees() {
        return getPosition() / 360;
    }

    @Override
    public double getPositionRotations() {
        return getPosition
    }

    @Override
    public double getVelocity() {
        return getVelocityRPS() * CLICKS_PER_ROTATION;
    }

    @Override
    public double getVelocityRPS() {
        return getVelocityRPM() * 60;
    }

    @Override
    public double getVelocityRPM() {
        return sparkMaxEncoder.getVelocity();
    }

    @Override
    public double setPosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double setPositionDegrees() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double setPositionRotations() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double setVelocity() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double setVelocityRPS() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double setVelocityRPM() {
        // TODO Auto-generated method stub
        return 0;
    }


}
