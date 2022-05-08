package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import org.texastorque.torquelib.motors.base.TorqueEncoderMotor;
import org.texastorque.torquelib.motors.base.TorqueMotor;
import org.texastorque.torquelib.motors.base.TorquePIDMotor;
import org.texastorque.torquelib.motors.base.TorqueSmartMotor;
import org.texastorque.torquelib.util.KPID;

/**
 * The Texas Torque wrapper for the SparkMax motor controller.
 * 
 * @author Justus Languell
 * @author Jack Pittenger
 */
public final class TorqueSparkMax extends TorqueMotor implements TorqueSmartMotor {
    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxAlternateEncoder alternateEncoder;
    private SparkMaxPIDController pidController;
    private SparkMaxAnalogSensor analogEncoder;
    private ArrayList<CANSparkMax> followers = new ArrayList<>();

    public final double CLICKS_PER_ROTATION;

    private double lastVelocity;
    private long lastVelocityTime;

    private double encoderZero = 0;

    /**
     * Construct a new TorqueSparkMax motor.
     * 
     * @param port The port (ID) of the motor.
     */
    public TorqueSparkMax(final int port) {
        super(port);

        this.lastVelocity = 0;
        this.lastVelocityTime = System.currentTimeMillis();
        motor = new CANSparkMax(port, MotorType.kBrushless);
        encoder = motor.getEncoder();
        analogEncoder = motor.getAnalog(SparkMaxAnalogSensor.Mode.kAbsolute);
        pidController = motor.getPIDController();
        CLICKS_PER_ROTATION = encoder.getCountsPerRevolution();
    }

    /**
     * Add a follower SparkMax.
     * 
     * @param port The port (ID) of the follower SparkMax.
     */
    @Override
    public void addFollower(final int port) {
        followers.add(new CANSparkMax(port, MotorType.kBrushless));
    }

    /**
     * Add a follower SparkMax and optionally invert.
     * 
     * @param port The port (ID) of the follower SparkMax.
     */
    @Override
    public void addFollower(final int port, final boolean invert) {
        CANSparkMax follower = new CANSparkMax(port, MotorType.kBrushless);
        follower.setInverted(invert);
        followers.add(follower);
    }

    /**
     * Sets the inversion status of the lead motor.
     * 
     * @param inverted To invert or not to invert.
     */
    @Override
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
        pidController.setP(kPID.getPGains());
        pidController.setI(kPID.getIGains());
        pidController.setD(kPID.getDGains());
        pidController.setFF(kPID.getFGains());
        if (kPID.getIZone() > 0)
            pidController.setIZone(kPID.getIZone());
        pidController.setOutputRange(kPID.getMin(), kPID.getMax());
    }

    /**
     * Sets the output of the motor to the given percent.
     * 
     * @param percent The percent the motor should output at.
     */
    @Override
    public void setPercent(final double percent) {
        motor.set(percent);
        for (CANSparkMax canSparkMax : followers)
            canSparkMax.follow(motor);
    }

    /**
     * Set the motor to output a certain voltage setpoint.
     * 
     * @param setpoint The voltage to output.
     */
    @Override
    public void setVoltage(final double setpoint) {
        motor.setVoltage(setpoint);
        for (CANSparkMax follower : followers)
            follower.follow(motor);
    }

    // Setters implemented from TorquePIDMotor

    /**
     * Set the motor's position in encoder units.
     * 
     * @param setpoint The encoder units to set the motor to.
     */
    @Override
    public void setPosition(final double setpoint) {
        setPositionRotations(setpoint / CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's position in degrees.
     * 
     * @param setpoint The degrees to set the motor to.
     */
    @Override
    public void setPositionDegrees(final double setpoint) {
        setPositionRotations(setpoint / 360);
    }

    /**
     * Set the motor's position in rotations.
     * 
     * @param setpoint The rotations to set the motor to.
     */
    @Override
    public void setPositionRotations(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kPosition);
            for (CANSparkMax follower : followers)
                follower.follow(motor);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
        }
    }

    /**
     * Set the motor's velocity in encoder units per second.
     * 
     * @param setpoint The encoder units per second to set the motor to.
     */
    @Override
    public void setVelocity(final double setpoint) {
        setVelocityRPS(setpoint / CLICKS_PER_ROTATION); 
    }

    /**
     * Set the motor's velocity in RPS.
     * 
     * @param setpoint The RPS to set the motor to.
     */
    @Override
    public void setVelocityRPS(final double setpoint) {
        setVelocityRPM(setpoint * 60);
    }

    /**
     * Set the motor's velocity in RPM.
     * 
     * @param setpoint The RPM to set the motor to.
     */
    @Override
    public void setVelocityRPM(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kVelocity);
            for (CANSparkMax follower : followers)
                follower.follow(motor);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
        } 
    }

    /**
     * Set velocity using feed forwarrd and smart motion profile... i think?
     * 
     * @param setpoint The velocity to set the motor to.
     * @param feedforward The feed forward to set the motor to.
     * @param units The feed forward units.
     */
    public void setFeedForwardSmartVelocity(final double setpoint, final double feedforward, final ArbFFUnits units) {
        try {
            pidController.setReference(setpoint, ControlType.kSmartVelocity, 0, feedforward, units);
            for (CANSparkMax follower : followers) 
                follower.follow(motor);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
        }
    }

    // Getters implemented from TorqueEncoderMotor

    /**
     * Get the position of the motor in encoder units.
     *
     * @return The position of the encoder in encoder units.
     */
    @Override
    public double getPosition() {
        return getPositionRotations() * CLICKS_PER_ROTATION;
    }
    
    /**
     * Get the position of the motor in degrees.
     *
     * @return The position of the encoder in degrees.
     */
    @Override
    public double getPositionDegrees() {
        return getPositionRotations() * 360;
    }

    /**
     * Get the position of the motor in rotations.
     *
     * @return The position of the encoder in rotations.
     */
    @Override
    public double getPositionRotations() {
        return encoder.getPosition() - encoderZero;
    }

    /**
     * Get the velocity of the motor in encoder units per second.
     * 
     * @return acceleration in encoder units per second.
     */
    @Override
    public double getVelocity() {
        return getVelocityRPS() * CLICKS_PER_ROTATION;
    }

    /**
     * Get the velocity of the motor in RPS.
     * 
     * @return acceleration in RPS.
     */
    @Override
    public double getVelocityRPS() {
        return getVelocityRPM() / 60;
    }

    /**
     * Get the velocity of the motor in RPM.
     * 
     * @return acceleration in RPM.
     */
    @Override
    public double getVelocityRPM() {
        return encoder.getVelocity();
    }

    /**
     * Get the acceleration of the motor in encoder units per second per second.
     * 
     * @return acceleration in encoder units per second per second.
     */
    @Override
    public double getAcceleration() {
        return getAccelerationRPS() * CLICKS_PER_ROTATION;
    }

      /**
     * Get the acceleration of the motor in RPS/s.
     * 
     * @return acceleration in RPM/s.
     */
    @Override
    public double getAccelerationRPS() {
        return getAccelerationRPM() / 60; 
    }

    /**
     * Get the acceleration of the motor in RPM/s.
     * 
     * @return acceleration in RPM/s.
     */
    @Override
    public double getAccelerationRPM() {
        final double currentVelocity = getVelocityRPM();
        final long currentTime = System.currentTimeMillis();

        final double acceleration = (currentVelocity - lastVelocity) / (currentTime - lastVelocityTime);

        lastVelocity = currentVelocity;
        lastVelocityTime = currentTime;

        return acceleration;
    }

    // Utility methods and SparkMax specific methods

    /**
     * Restores the lead SparkMax to factory defaults.
     */
    public void restoreFactoryDefaults() {
        motor.restoreFactoryDefaults();
    }

     /**
     * @apiNote UNSAFE
     */
    public void enableVoltageCompensation() {
        motor.enableVoltageCompensation(2);
        for (CANSparkMax follower : followers) {
            follower.enableVoltageCompensation(2);
        }
    }

    /**
     * @apiNote UNSAFE
     */
    public void disableVoltageCompensation() {
        motor.disableVoltageCompensation();
        for (CANSparkMax follower : followers) {
            follower.disableVoltageCompensation();
        }

    }

    /** 
     * Gets voltage used by the SparkMax.
     * 
     * @return The voltage used by the SparkMax.
     */
    public double getVoltage() {
        return motor.getBusVoltage();
    }

    /**
     * Gets current used by the SparkMax.
     * 
     * @return The current used by the SparkMax.
     */
    public double getCurrent() {
        return motor.getOutputCurrent();
    }

    /**
     * Set the motor to output a certain current setpoint.
     * 
     * @param setpoint The current to output.
     */
    public void setCurrent(final double setpoint) {
        pidController.setReference(setpoint, ControlType.kCurrent);
        for (CANSparkMax follower : followers)
            follower.follow(motor);
    }

    /**
     * Burns the SparkMax flash.
     */
    public void burnFlash() {
        motor.burnFlash();
    }

    /** 
     * Configures an I-Zone on PID.
     * 
     * @param iZone The I-Zone value to set.
     * 
     * @deprecated I-Zone is now included in KPID.
     */
    @Deprecated
    public void configureIZone(final double iZone) {
        pidController.setIZone(iZone);
    }

    /**
     * Set a supply limit for the SparkMax.
     * 
     * @param limit max amps.
     */

    public void setSupplyLimit(final int limit) {
        REVLibError e = motor.setSmartCurrentLimit(limit);
        if (e != REVLibError.kOk)
            System.out.printf("TorqueSparkMax port %d: Error configuring supply limit: %s\n", port, e.name());


    }

    // Smart motion functions.

    /**
     * Configure needed variables for smart motion.
     * 
     * - setSmartMotionMaxVelocity() will limit the velocity in RPM of the pid
     * controller in Smart Motion mode - setSmartMotionMinOutputVelocity() will put
     * a lower bound in RPM of the pid controller in Smart Motion mode -
     * setSmartMotionMaxAccel() will limit the acceleration in RPM^2 of the pid
     * controller in Smart Motion mode - setSmartMotionAllowedClosedLoopError() will
     * set the max allowed error for the pid controller in Smart Motion mode
     * 
     * @param maxVelocity     the max velocity
     * @param minVelocity     the min velocity
     * @param maxAcceleration the maxAcceleration
     * @param allowedError    the allowed amount of error
     * @param id              the id for the pid (usually 0)
     *  
     * @author Jack Pittenger 
     */
    public void configureSmartMotion(final double maxVelocity, 
                                     final double minVelocity, 
                                     final double maxAcceleration,
                                     final double allowedError, 
                                     final int id) {
        pidController.setSmartMotionMaxVelocity(maxVelocity, id);
        pidController.setSmartMotionMinOutputVelocity(minVelocity, id);
        pidController.setSmartMotionMaxAccel(maxAcceleration, id);
        pidController.setSmartMotionAllowedClosedLoopError(allowedError, id);
    }

    // Sparkmax specific CAN utilization reduction functions.
    // Only use these methods if you know what you are doing.

    /**
     * Configures CAN frames to be quick on the lead motor.
     * 
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public void configureFastLeader() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 2);
    }

    /**
     * Configure the CAN frames for a "dumb motor," which won't need to access CAN
     * data often or at all.
     * 
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public void configureDumbCANFrame() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 200);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 1000);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 1000);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 2000);
    }

    /**
     * Configure the CAN frames for a "dumb motor" leader, which won't give data
     * often but will update fast for its follower.
     * 
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public void configureDumbLeaderCANFrame() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 20);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 1000);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 1000);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 2000);
    }

    /**
     * Configures the CAN frame for a no-follower encoder-positional only sparkmax;
     * such as would be in a climber.
     * 
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public void configurePositionalCANFrame() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 143);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 500);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 20);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 1000);
    }

    /**
     * Reduce the CAN frame interval for a follower.
     * 
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public void lowerFollowerCANFrame() {
        for (CANSparkMax follower : followers) {
            follower.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 100);
            follower.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 500);
            follower.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 500);
            follower.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 1000);
        }
    }
}
