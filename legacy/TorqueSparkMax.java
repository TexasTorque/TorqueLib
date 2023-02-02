/**
 * Copyright 2011-2023 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.legacy;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.legacy.base.TorqueMotor;
import org.texastorque.torquelib.legacy.base.TorqueSmartMotor;
import org.texastorque.torquelib.util.TorqueUtil;

/**
 * The Texas Torque wrapper for the SparkMax motor controller.
 *
 * @author Justus Languell
 * @author Jack Pittenger
 * 
 * @deprecated NOT SUITABLE FOR 2023
 */
@Deprecated
public final class TorqueSparkMax extends TorqueMotor implements TorqueSmartMotor {
    private CANSparkMax motor;
    private RelativeEncoder encoder;
    private SparkMaxAlternateEncoder alternateEncoder;
    private SparkMaxPIDController pidController;
    private SparkMaxAnalogSensor analogEncoder;
    private ArrayList<TorqueSparkMax> followers = new ArrayList<>();

    /**
     * Clicks per rotation on the motor encoder.
     */
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
    public final void addFollower(final int port) {
        followers.add(new TorqueSparkMax(port));
    }

    /**
     * Add a follower SparkMax and optionally invert.
     *
     * @param port The port (ID) of the follower SparkMax.
     */
    @Override
    public final void addFollower(final int port, final boolean invert) {
        TorqueSparkMax follower = new TorqueSparkMax(port);
        follower.invert(invert);
        followers.add(follower);
    }

    /**
     * Sets the inversion status of the lead motor.
     *
     * @param invert To invert or not to invert.
     */
    @Override
    public final void invert(final boolean invert) {
        motor.setInverted(invert);
    }

    /**
     * Set the motor's position in encoder units.
     *
     * @param setpoint The encoder units to set the motor to.
     */
    @Override
    public final void setPosition(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kPosition);
            for (TorqueSparkMax follower : followers)
                follower.setPosition(setpoint);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
        }
        encoder.setPosition(0);
    }

    /**
     * Configures the PID controller for the motor.
     *
     * @param kPID The KPID value to configure the motor too.
     */
    @Override
    @Deprecated
    public final void configurePID(final KPID kPID) {
        pidController.setP(kPID.getPGains());
        pidController.setI(kPID.getIGains());
        pidController.setD(kPID.getDGains());
        pidController.setFF(kPID.getFGains());
        if (kPID.getIZone() > 0)
            pidController.setIZone(kPID.getIZone());
        pidController.setOutputRange(kPID.getMin(), kPID.getMax());
    }

    /**
     * Configures the PID controller for the motor.
     *
     * @param pid The PID to configure the motor with.
     */
    @Override
    public final void configurePID(final TorquePID pid) {
        pidController.setP(pid.getProportional());
        pidController.setI(pid.getIntegral());
        pidController.setD(pid.getDerivative());
        pidController.setFF(pid.getFeedForward());
        if (pid.hasIntegralZone())
            pidController.setIZone(pid.getIntegralZone());
        pidController.setOutputRange(pid.getMinOutput(), pid.getMaxOutput());
    }

    /**
     * Sets the output of the motor to the given percent.
     *
     * @param percent The percent the motor should output at.
     */
    @Override
    public final void setPercent(final double percent) {
        motor.set(percent);
        for (TorqueSparkMax follower : followers)
            follower.setPercent(percent);
    }

    /**
     * Set the motor to output a certain voltage setpoint.
     *
     * @param setpoint The voltage to output.
     */
    @Override
    public final void setVoltage(final double setpoint) {
        motor.setVoltage(setpoint);
        for (TorqueSparkMax follower : followers)
            follower.setVoltage(setpoint);
    }

    // Setters implemented from TorquePIDMotor 

    /**
     * Set the motor's position in degrees.
     *
     * @param setpoint The degrees to set the motor to.
     */
    @Override
    public final void setPositionDegrees(final double setpoint) {
        TorqueUtil.notImplemented();
    }

    /**
     * Set the motor's position in rotations.
     *
     * @param setpoint The rotations to set the motor to.
     */
    @Override
    public final void setPositionRotations(final double setpoint) {
        TorqueUtil.notImplemented();
    }

    /**
     * Set the motor's velocity in encoder units per second.
     *
     * @param setpoint The encoder units per second to set the motor to.
     */
    @Override
    public final void setVelocity(final double setpoint) {
        setVelocityRPS(setpoint / CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's velocity in RPS.
     *
     * @param setpoint The RPS to set the motor to.
     */
    @Override
    public final void setVelocityRPS(final double setpoint) {
        setVelocityRPM(setpoint * 60);
    }

    /**
     * Set the motor's velocity in RPM.
     *
     * @param setpoint The RPM to set the motor to.
     */
    @Override
    public final void setVelocityRPM(final double setpoint) {
        try {
            pidController.setReference(setpoint, ControlType.kVelocity);
            for (TorqueSparkMax follower : followers)
                follower.setVelocityRPM(setpoint);
        } catch (Exception e) {
            System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
        }
    }

    // /**
    //  * Set velocity using feed forwarrd and smart motion profile... i think?
    //  *
    //  * @param setpoint The velocity to set the motor to.
    //  * @param feedforward The feed forward to set the motor to.
    //  * @param units The feed forward units.
    //  */
    // public final void setFeedForwardSmartVelocity(final double setpoint, final double feedforward,
    //         final ArbFFUnits units) {
    //     try {
    //         pidController.setReference(setpoint, ControlType.kSmartVelocity, 0, feedforward, units);
    //         for (TorqueSparkMax follower : followers)
    //             follower.setFeedForwardSmartVelocity(setpoint, feedforward);
    //     } catch (Exception e) {
    //         System.out.printf("TorqueSparkMax port %d: You need to configure the PID\n", port);
    //     }
    // }

    // Getters implemented from TorqueEncoderMotor

    /**
     * Get the position of the motor in encoder units.
     *
     * @return The position of the encoder in encoder units.
     */
    @Override
    public final double getPosition() {
        return encoder.getPosition() - encoderZero;
    }

    /**
     * Get the position of the motor in degrees.
     *
     * @return The position of the encoder in degrees.
     */
    @Override
    public final double getPositionDegrees() {
        return getPosition() * 360 / CLICKS_PER_ROTATION;
    }

    /**
     * Get the position of the motor in rotations.
     *
     * @return The position of the encoder in rotations.
     */
    @Override
    public final double getPositionRotations() {
        TorqueUtil.notImplemented();
        return 0;
    }

    /**
     * Get the velocity of the motor in encoder units per second.
     *
     * @return acceleration in encoder units per second.
     */
    @Override
    public final double getVelocity() {
        return getVelocityRPS() * CLICKS_PER_ROTATION;
    }

    /**
     * Get the velocity of the motor in RPS.
     *
     * @return acceleration in RPS.
     */
    @Override
    public final double getVelocityRPS() {
        return getVelocityRPM() / 60;
    }

    /**
     * Get the velocity of the motor in RPM.
     *
     * @return acceleration in RPM.
     */
    @Override
    public final double getVelocityRPM() {
        return encoder.getVelocity();
    }

    /**
     * Get the acceleration of the motor in encoder units per second per second.
     *
     * @return acceleration in encoder units per second per second.
     */
    @Override
    public final double getAcceleration() {
        return getAccelerationRPS() * CLICKS_PER_ROTATION;
    }

    /**
     * Get the acceleration of the motor in RPS/s.
     *
     * @return acceleration in RPM/s.
     */
    @Override
    public final double getAccelerationRPS() {
        return getAccelerationRPM() / 60;
    }

    /**
     * Get the acceleration of the motor in RPM/s.
     *
     * @return acceleration in RPM/s.
     */
    @Override
    public final double getAccelerationRPM() {
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
    public final void restoreFactoryDefaults() {
        motor.restoreFactoryDefaults();
    }

    // /**
    //  * @apiNote UNSAFE
    //  */
    // public final void enableVoltageCompensation() {
    //     motor.enableVoltageCompensation(2);
    //     for (TorqueSparkMax follower : followers) {
    //         follower.enableVoltageCompensation(2);
    //     }
    // }

    /**
     * @apiNote UNSAFE
     */
    public final void disableVoltageCompensation() {
        motor.disableVoltageCompensation();
        for (TorqueSparkMax follower : followers) {
            follower.disableVoltageCompensation();
        }
    }

    /**
     * Gets voltage used by the SparkMax.
     *
     * @return The voltage used by the SparkMax.
     */
    public final double getVoltage() {
        return motor.getBusVoltage();
    }

    /**
     * Gets current used by the SparkMax.
     *
     * @return The current used by the SparkMax.
     */
    public final double getCurrent() {
        return motor.getOutputCurrent();
    }

    /**
     * Set the motor to output a certain current setpoint.
     *
     * @param setpoint The current to output.
     */
    public final void setCurrent(final double setpoint) {
        pidController.setReference(setpoint, ControlType.kCurrent);
        for (TorqueSparkMax follower : followers)
            follower.setCurrent(setpoint);
    }

    /**
     * Burns the SparkMax flash.
     */
    public final void burnFlash() {
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
    public final void configureIZone(final double iZone) {
        pidController.setIZone(iZone);
    }

    /**
     * Set a supply limit for the SparkMax.
     *
     * @param limit max amps.
     */

    public final void setSupplyLimit(final int limit) {
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
    public final void configureSmartMotion(final double maxVelocity, final double minVelocity,
            final double maxAcceleration, final double allowedError, final int id) {
        var r = pidController.setSmartMotionMaxVelocity(maxVelocity, id);
        System.out.println(r);
        r = pidController.setSmartMotionMinOutputVelocity(minVelocity, id);
        System.out.println(r);
        r = pidController.setSmartMotionMaxAccel(maxAcceleration, id);
        System.out.println(r);
        r = pidController.setSmartMotionAllowedClosedLoopError(allowedError, id);
        System.out.println(r);
    } //TODO: @Juicestus Format at home
    //controller.setReference
    // Sparkmax specific CAN utilization reduction functions.
    // Only use these methods if you know what you are doing.

    /**
     * Configures CAN frames to be quick on the lead motor.
     *
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public final void configureFastLeader() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 2);
    }

    /**
     * Configure the CAN frames for a "dumb motor," which won't need to access CAN
     * data often or at all.
     *
     * @apiNote Only use these methods if you know what you are doing.
     * @author Jack Pittenger
     */
    public final void configureDumbCANFrame() {
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
    public final void configureDumbLeaderCANFrame() {
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
    public final void configurePositionalCANFrame() {
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
    public final void lowerFollowerCANFrame() {
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 100);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 500);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 500);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 1000);
        for (TorqueSparkMax follower : followers) {
            follower.lowerFollowerCANFrame();
        }
    }

    public final void setPositionConversionFactor(double positionFactor) {
        encoder.setPositionConversionFactor(positionFactor);
    }

    public final void setVelocityConversionFactor(double velocityFactor) {
        encoder.setVelocityConversionFactor(velocityFactor);
    }

    public final void setEncoderZero(final double position) {
        this.encoderZero = position + getPosition();
    }

    // public final ArrayList<CANSparkMax> getCanSparkMax() {
    //     ArrayList<CANSparkMax> list = new ArrayList<>();
    //     list.add(motor);
    //     list.addAll(followers);
    //     return list;
    // }
}
