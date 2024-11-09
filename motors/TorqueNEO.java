/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import org.texastorque.torquelib.control.TorqueDebug;
import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.util.TorqueUtil;
import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.MotorFeedbackSensor;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

/**
 * Designed to be the one and only motor wrapper for 2023.
 *
 * This is a loose wrapper, designed only to:
 * - organize the REVLib objects.
 * - provide more distinct and consise get and set methods.
 * - handle REVLib errors.
 *
 * @author Justus Languell
 */
public final class TorqueNEO {

    /**
     * Collected representation of the SmartMotionProfile parameters.
     *
     * @author Justus Languell
     */
    public static final class SmartMotionProfile {
        public final double maxVelocity, minVelocity, maxAcceleration, allowedError;
        public final int slot;

        public SmartMotionProfile(final double maxVelocity, final double minVelocity, final double maxAcceleration,
                                  final double allowedError) {
            this(maxVelocity, minVelocity, maxAcceleration, allowedError, 0);
        }

        public SmartMotionProfile(final double maxVelocity, final double minVelocity, final double maxAcceleration,
                                  final double allowedError, final int slot) {
            this.maxVelocity = maxVelocity;
            this.minVelocity = minVelocity;
            this.maxAcceleration = maxAcceleration;
            this.allowedError = allowedError;
            this.slot = slot;
        }
    }
    /**
     * The internal components to the motor wrapper.
     * These are marked public for a reason. Since
     * they are marked final their reference cannot
     * be set from outside, but they can be accessed
     * and mutated from outside just like a get method.
     */
    public final CANSparkMax motor;
    public final RelativeEncoder encoder;
    public final SparkPIDController controller;

    // ****************
    // * DEVICE SETUP *
    // ****************

    public final ArrayList<CANSparkMax> followers;

    /**
     * Build a new TorqueNEO.
     *
     * @param id The CAN ID of the motor.
     * @param encoderType The type of encoder to use.
     * @param inverted Whether or not the motor is inverted.
     * @param followers The CAN IDs of the followers.
     */
    public TorqueNEO(final int id) {
        motor = new CANSparkMax(id, MotorType.kBrushless);
        encoder = motor.getEncoder();
        controller = motor.getPIDController();
        followers = new ArrayList<>();
        invertMotor(false);
    }

    public TorqueNEO debug(final String name) {
        new TorqueDebug(this, name);
        return this;
    }

    public void setBreakMode(final boolean isBreak) {
        motor.setIdleMode(isBreak ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
        for (final var follower : followers) {
            follower.setIdleMode(isBreak ? CANSparkMax.IdleMode.kBrake : CANSparkMax.IdleMode.kCoast);
        }
    }

    public void invertMotor(final boolean invert) {
        motor.setInverted(invert);
    }

    public void setPositionConversionFactor(final double factor) { encoder.setPositionConversionFactor(factor); }

    public void setVelocityConversionFactor(final double factor) { encoder.setVelocityConversionFactor(factor); }

    public void setConversionFactors(final double posFactor, final double veloFactor) {
        setPositionConversionFactor(posFactor);
        setVelocityConversionFactor(veloFactor);
    }

    /**
     * Add a follower motor ID.
     *
     * @param id The CAN ID of the follower.
     * @param invert Is the follower inverted relative to the leader?
     */
    public void addFollower(final int id, final boolean invert) {
        followers.add(new CANSparkMax(id, MotorType.kBrushless));
        followers.get(followers.size() - 1).follow(motor, invert);
    }

    // ********************************
    // * VOLTAGE AND CURRENT CONTROLS *
    // ********************************

    public void burnFlash() { motor.burnFlash(); }

    /**
     * Set the motor to a percent output from a decimal.
     * Domain is [-1, 1] where 0 is off and negative values are reversed.
     *
     * @param percent The percent output as a decimal.
     */
    public void setPercent(final double percent) { motor.set(percent); }

    public double getPercent() { return motor.getAppliedOutput(); }

    public SparkPIDController getPIDController() { return controller; }

    /**
     * Set the motor to a voltage output.
     * Domain is [-12, 12] where 0 is off and negative values are reversed
     * assuming max volts of 12.
     *
     * @param percent The voltage output.
     */
    public void setVolts(final double volts) { motor.setVoltage(volts); }

    public double getBusVoltage() { return motor.getBusVoltage(); }

    public double getVolts() { return motor.getBusVoltage() * motor.getAppliedOutput(); }

    /**
     * Set the voltage compensation for the motor.
     *
     * @param volts Voltage compensation.
     */
    public void setVoltageCompensation(final double volts) { checkError(motor.enableVoltageCompensation(volts)); }

     /**
     * Disable the voltage compensation for the motor.
     *
     */
    public void disableVoltageCompensation() { checkError(motor.disableVoltageCompensation()); }

    /**
     * Set the motor to a amperage output.
     * Domain is [-40, 40] where 0 is off and negative values are reversed
     * assuming max amperage of 40.
     *
     * @param percent The amperage output.
     */
    public void setCurrent(final double current) { checkError(controller.setReference(current, CANSparkBase.ControlType.kCurrent)); }

    public double getCurrent() { return motor.getOutputCurrent(); }

    // **********************************
    // * POSITION AND VELOCITY CONTROLS *
    // **********************************

    // This variable is used to keep track of the last set current limit for
    // observing when changes need to be made.
    private double lastSetCurrentLimit = -1;
    /**
     * Set the maximum current the motor can draw.
     *
     * @param amps Maximum amperage.
     */
    public void setCurrentLimit(final int amps) { 
        boolean didErr = checkError(motor.setSmartCurrentLimit(amps)); 
        if (!didErr) {
            lastSetCurrentLimit = amps;
        }
    }

    /**
     * Safely set the maximum current the motor can draw during the update loop.
     * This can be called repeatly to set the maximum current but not overrun
     * CAN utalization. This is because it works using an observer.
     *
     * @param amps Maximum amperage.
     */
    public void setCurrentLimitUpdatable(final int amps) {
        if (amps == lastSetCurrentLimit) return;
        setCurrentLimit(amps);
    }

    /**
     * Configure the PID parameters.
     * Necessary to use position and velocity control.
     *
     * @param pid The TorquePID object.
     */
    public void configurePID(final TorquePID pid) {
        checkError(controller.setP(pid.getProportional()), "p term");
        checkError(controller.setI(pid.getIntegral()), "i term");
        checkError(controller.setD(pid.getDerivative()), "d term");
        if (pid.hasIntegralZone()) checkError(controller.setIZone(pid.getIntegralZone()), "i zone");
        checkError(controller.setFF(pid.getFeedForward()), "ff term");
        checkError(controller.setOutputRange(pid.getMinOutput(), pid.getMaxOutput()), "output range");
    } 

    /**
     * Configure the PID parameters.
     * Necessary to use position and velocity control.
     *
     */
    public void configurePID(final double p, final double i, final double d, final double iZone, final double ff, final double minOutput, final double maxOutput) {
        checkError(controller.setP(p), "p term");
        checkError(controller.setI(i), "i term");
        checkError(controller.setD(d), "d term");
        checkError(controller.setIZone(iZone), "i zone");
        checkError(controller.setFF(ff), "ff term");
        checkError(controller.setOutputRange(minOutput, maxOutput), "output range");
    }

    /**
     * Configure the PID parameters.
     * Necessary to use position and velocity control.
     *
     */
    public void configurePIDF(final double p, final double i, final double d, final double ff) {
        checkError(controller.setP(p), "p term");
        checkError(controller.setI(i), "i term");
        checkError(controller.setD(d), "d term");
        checkError(controller.setFF(ff), "ff term");
    }

    public void setPIDVoltageLimits(final double min, final double max) { checkError(controller.setOutputRange(min, max)); }

    public void setPIDFeedbackDevice(final MotorFeedbackSensor device) { checkError(controller.setFeedbackDevice(device)); }

    public void setPIDReference(final double goal, final CANSparkBase.ControlType control) { checkError(controller.setReference(goal, control)); }

    /**
     * Default unit is rotations, changed with setConversionFactors method.
     *
     * @param pos The position to set.
     */
    public void setPosition(final double pos) { checkError(controller.setReference(pos, CANSparkBase.ControlType.kPosition)); }

    /**
     * Default unit is rotations, changed with setConversionFactors method.
     *
     * @return The position.
     */
    public double getPosition() { return encoder.getPosition(); }

    /**
     * 
     * @return The Absolute Encoder object attatched to the SparkMax through a breakout board.
     */
    public AbsoluteEncoder getAbsoluteEncoder(Type type) { return motor.getAbsoluteEncoder(type); }


    /**
     * Default unit is RPM, changed with setConversionFactors method.
     *
     * @param velo The velocity to set.
     */
    public void setVelocity(final double velo) { checkError(controller.setReference(velo, CANSparkBase.ControlType.kVelocity)); }

    // *************************
    // * SMART MOTION CONTROLS *
    // *************************

    /**
     * Default unit is RPM, changed with setConversionFactors method.
     *
     * @return The velocity.
     */
    public double getVelocity() { return encoder.getVelocity(); }

    /**
     * Configure the SmartMotionProfile parameters.
     * Necessary to use the smart motion control.
     *
     * @param profile The SmartMotionProfile object.
     */
    public void configureSmartMotion(final SmartMotionProfile profile) {
        checkError(controller.setSmartMotionMaxVelocity(profile.maxVelocity, profile.slot), "max velocity");
        checkError(controller.setSmartMotionMinOutputVelocity(profile.minVelocity, profile.slot), "min velocity");
        checkError(controller.setSmartMotionMaxAccel(profile.maxAcceleration, profile.slot), "max acceleration");
        checkError(controller.setSmartMotionAllowedClosedLoopError(profile.allowedError, profile.slot),
                   "allowed error");
    }

    /**
     * Default unit is rotations, changed with setConversionFactors method.
     *
     * @param pos The position to set.
     */
    public void setSmartPosition(final double pos) {
        checkError(controller.setReference(pos, CANSparkBase.ControlType.kSmartMotion));
    }

    /**
     * Default unit is RPM, changed with setConversionFactors method.
     *
     * @param velo The velocity to set.
     */
    public void setSmartVelocity(final double velo) {
        checkError(controller.setReference(velo, CANSparkBase.ControlType.kSmartVelocity));
    }

    // *********************
    // * UTILITY FUNCTIONS *
    // *********************

    /**
     * Check error with non–applicable field name.
     *
     * @param error REVLibError to check.
     * 
     * @return True if there has been an error, false if the function has passed.
     */
    private boolean checkError(final REVLibError error) { return checkError(error, "N/A"); }

    /**
     * Check error with applicable field name.
     *
     * @param error REVLibError to check.
     * @param field The field name.
     * 
     * @return True if there has been an error, false if the function has passed.
     */
    private boolean checkError(final REVLibError error, final String field) {
        if (error == REVLibError.kOk) return false;

        final var parent = TorqueUtil.getStackTraceElement(3);
        System.err.printf("TorqueNEO Error\n"
                                  + "\tID = %d\n"
                                  + "\tField = %s\n"
                                  + "\tMethod = %s\n"
                                  + "\tError = %s\n",
                          motor.getDeviceId(), field, parent.getMethodName(), error.toString());

        return true;
    }
}
