package org.texastorque.torquelib.motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import java.util.ArrayList;
import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.util.TorqueUtil;

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
     * The internal components to the motor wrapper.
     * These are marked public for a reason. Since
     * they are marked final their reference cannot
     * be set from outside, but they can be accessed
     * and mutated from outside just like a get method.
     */
    public final CANSparkMax motor;
    public final RelativeEncoder encoder;
    public final SparkMaxPIDController controller;
    public final ArrayList<CANSparkMax> followers;

    // ****************
    // * DEVICE SETUP *
    // ****************

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

    /**
     * Set the motor to a percent output from a decimal. 
     * Domain is [-1, 1] where 0 is off and negative values are reversed.
     * 
     * @param percent The percent output as a decimal.
     */
    public void setPercent(final double percent) {
        motor.set(percent);
    }

    public double getPercent() {
        return motor.getAppliedOutput();
    }

    /**
     * Set the motor to a voltage output. 
     * Domain is [-12, 12] where 0 is off and negative values are reversed
     * assuming max volts of 12.
     * 
     * @param percent The voltage output.
     */
    public void setVolts(final double volts) {
        motor.setVoltage(volts);
    }

    public double getVolts() {
        return motor.getBusVoltage();
    }

     /**
     * Set the motor to a amperage output. 
     * Domain is [-40, 40] where 0 is off and negative values are reversed
     * assuming max amperage of 40.
     * 
     * @param percent The amperage output.
     */
    public void setCurrent(final double current) {
        checkError(controller.setReference(current, ControlType.kCurrent));
    }

    public double getCurrent() {
        return motor.getOutputCurrent();
    }

    /**
     * Set the maximum current the motor can draw.
     * 
     * @param amps Maximum amperage.
     */
    public void setCurrentLimit(final int amps) {
        checkError(motor.setSmartCurrentLimit(amps));
    }

    // **********************************
    // * POSITION AND VELOCITY CONTROLS *
    // **********************************
   
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
        if (pid.hasIntegralZone())
            checkError(controller.setIZone(pid.getIntegralZone()), "i zone");
        checkError(controller.setFF(pid.getFeedForward()), "ff term");
        checkError(controller.setOutputRange(pid.getMinOutput(), pid.getMaxOutput()), "output range");
    }

    public void setPositionRotations(final double rotations) {
        checkError(controller.setReference(rotations, ControlType.kPosition));
    }

    public double getPositionRotations() {
        return encoder.getPosition();
    }

    public void setPositionDegrees(final double degrees) {
        checkError(controller.setReference(degrees / 360, ControlType.kVelocity));
    }

    public double getRotationDegrees() {
        return getPositionRotations() * 360;
    }

    public void setVelocityRPM(final double rpm) {
        checkError(controller.setReference(rpm, ControlType.kVelocity));
    }

    public double getVelocityRPM() {
        return encoder.getVelocity();
    }

    public void setVelocityRPS(final double rps) {
        checkError(controller.setReference(rps * 60, ControlType.kVelocity));
    }

    public double getVelocityRPS() {
        return getVelocityRPM() / 60;
    }

    // *************************
    // * SMART MOTION CONTROLS *
    // *************************

    /**
     * Collected representation of the SmartMotionProfile parameters.
     * 
     * @author Justus Languell
     */
    public static final class SmartMotionProfile {
        public final double maxVelocity, minVelocity, maxAcceleration, allowedError;
        public final int slot;

        public SmartMotionProfile(final double maxVelocity, final double minVelocity, 
                final double maxAcceleration, final double allowedError) {
            this(maxVelocity, minVelocity, maxAcceleration, allowedError, 0);
        }

        public SmartMotionProfile(final double maxVelocity, final double minVelocity, 
                final double maxAcceleration, final double allowedError, final int slot) {
            this.maxVelocity = maxVelocity;
            this.minVelocity = minVelocity;
            this.maxAcceleration = maxAcceleration;
            this.allowedError = allowedError;
            this.slot = slot;
        }
    }

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
        checkError(controller.setSmartMotionAllowedClosedLoopError(profile.allowedError, profile.slot), "allowed error");
    }

    public void setSmartPositionRotations(final double rotations) {
        checkError(controller.setReference(rotations, ControlType.kSmartMotion));
    }

    public void setSmartPositionDegrees(final double degrees) {
        checkError(controller.setReference(degrees / 360, ControlType.kSmartMotion));
    }

    public void setSmartVelocityRPM(final double rpm) {
        checkError(controller.setReference(rpm, ControlType.kSmartVelocity));
    }

    public void setSmartVelocityRPS(final double rps) {
        checkError(controller.setReference(rps * 60, ControlType.kSmartVelocity));
    }

    // *********************
    // * UTILITY FUNCTIONS *
    // *********************

    /**
     * Check error with non–applicable field name.
     * 
     * @param error REVLibError to check.
     */
    private void checkError(final REVLibError error) {
        checkError(error, "N/A");
    }

    /**
     * Check error with applicable field name.
     * 
     * @param error REVLibError to check.
     * @param field The field name.
     */
    private void checkError(final REVLibError error, final String field) {
        if (error == REVLibError.kOk) return;

        final var parent = TorqueUtil.getStackTraceElement(3);
        System.err.printf("TorqueNEO Error\n"
                + "\tID = %d\n"
                + "\tField = %s\n"
                + "\tMethod = %s\n" 
                + "\tError = %s\n",
                motor.getDeviceId(), parent.getMethodName(), error.toString());
    }

}
