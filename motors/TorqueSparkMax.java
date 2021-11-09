package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import com.revrobotics.CANAnalog;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.texastorque.torquelib.motors.base.*;
import org.texastorque.torquelib.util.KPID;

/** 
 * An improved version of the legacy TorqueSparkMax class.
 * 
 * Why is SparkMax like this... 
 * 
 * @author Jack
 * @apiNote Original version created for 2020 season.
 * @author Justus
 * @apiNote Remade for 2022 season with TorqueLib refactoring.
 */
public class TorqueSparkMax extends TorqueMotor implements TorqueEncoderMotor, TorquePIDMotor {

    private CANSparkMax sparkMax;
    private CANEncoder sparkMaxEncoder;
    private CANEncoder alternateEncoder;
    private CANAnalog analogEncoder;
    private ArrayList<CANSparkMax> sparkMaxFollowers = new ArrayList<>();
    private CANPIDController pid;

    private final String pidNotSet;

    private double encoderZero = 0;

    /**
     * Creates a new TorqueSparkMax motor.
     * 
     * @param port The port the motor is plugged into
     */
    public TorqueSparkMax(int port) {
        this.port = port;
        sparkMax = new CANSparkMax(port, MotorType.kBrushless);
        sparkMaxEncoder = sparkMax.getEncoder();
        analogEncoder = sparkMax.getAnalog(AnalogMode.kAbsolute);
        pidNotSet = "[NeoSparkMax](" + port + ") PID not configured, please set PID";
    } 

    @Override
    public void addFollower(int port) {
        sparkMaxFollowers.add(new CANSparkMax(port, MotorType.kBrushless));
    }

    @Override
    public void set(double output) {
        sparkMax.set(output);
        for (CANSparkMax canSparkMax : sparkMaxFollowers) {
            canSparkMax.follow(sparkMax);
        } 
    }

    public void set(double output, ControlType ctrlType) {
        try {
            pid.setReference(output, ctrlType);
            for (CANSparkMax follower : sparkMaxFollowers) {
                follower.follow(sparkMax, invert);
            } 
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(pidNotSet);
        }
    }

    @Override
    public void configurePID(KPID kPID) {
        pid = sparkMax.getPIDController();
        pid.setP(kPID.p());
        pid.setI(kPID.i());
        pid.setD(kPID.d());
        pid.setFF(kPID.f());
        pid.setOutputRange(kPID.min(), kPID.max());
    } 

    @Override
    public void updatePID(KPID kPID) {
        pid.setP(kPID.p());
        pid.setI(kPID.i());
        pid.setD(kPID.d());
        pid.setFF(kPID.f());
        pid.setOutputRange(kPID.min(), kPID.max());
    }
    
}
