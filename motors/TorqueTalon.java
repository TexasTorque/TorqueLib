package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxmotor;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;

import org.texastorque.torquelib.motors.base.TorqueEncoderMotor;
import org.texastorque.torquelib.motors.base.TorqueMotor;
import org.texastorque.torquelib.motors.base.TorquePIDMotor;
import org.texastorque.torquelib.util.KPID;

/**
 * The Texas Torque wrapper for the SparkMax motor controller.
 * 
 * @author Justus Languell
 * @author Jack Pittenger
 */
public class TorqueTalon extends TorqueMotor implements TorquePIDMotor, TorqueEncoderMotor {
    private WPI_TalonSRX motor;
    private ArrayList<WPI_TalonSRX> followers = new ArrayList<>();

    private final double CLICKS_PER_ROTATION = 4096;

    private double lastVelocity;
    private long lastVelocityTime;

    private double encoderZero = 0;

    /**
     * Construct a new TorqueSparkMax motor.
     * 
     * @param port The port (ID) of the motor.
     */
    public TorqueTalon(final int port) {
        super(port);

        this.lastVelocity = 0;
        this.lastVelocityTime = System.currentTimeMillis();
        motor = new WPI_TalonSRX(port);
    }

    /**
     * Add a follower SparkMax.
     * 
     * @param port The port (ID) of the follower SparkMax.
     */
    @Override
    public void addFollower(final int port) {
        WPI_TalonSRX follower = new WPI_TalonSRX(port);
        follower.follow(motor);
        followers.add(follower);
    }

    /**
     * Sets the inversion status of the lead motor.
     * 
     * @param inverted To invert or not to invert.
     */
    public void invert(final boolean invert) {
        motor.setInverted(invert);
    }

    /**
     * Configures the PID controller for the motor.
     * 
     * @param kPID The KPID value to configure the motor too.
     */
    @Override
    public void configurePID(final KPID kPID) {
        motor.config_kP(0, kPID.getPGains());
        motor.config_kI(0, kPID.getIGains());
        motor.config_kD(0, kPID.getDGains());
        motor.config_kF(0, kPID.getFGains());
        double iZone;
        if ((iZone = kPID.getIZone()) > 0)
            motor.config_IntegralZone(0, kPID.getIZone());
        motor.configPeakOutputForward(kPID.getMax());
        motor.configPeakOutputReverse(kPID.getMin());
    }

      /**
     * Sets the output of the motor to the given percent.
     * 
     * @param percent The percent the motor should output at.
     */
    @Override
    public void setPercent(final double percent) {
        motor.set(ControlMode.PercentOutput, percent);
        for (WPI_TalonSRX follower : followers)
            follower.set(ControlMode.Follower, port);
    }



    



}