package org.texastorque.torquelib.motors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAlternateEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder;
import java.util.ArrayList;
import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.util.TorqueUtil;

/**
 * Designed to be the one and only motor wrapper for 2023.
 *
 * @author Justus Languell
 */
public final class TorqueNEO {

    private final CANSparkMax motor;

    public CANSparkMax getMotor() {
        return motor;
    }

    private final RelativeEncoder encoder;

    public RelativeEncoder getEncoder() {
        return encoder;
    }

    private final SparkMaxPIDController controller;

    public SparkMaxPIDController getController() {
        return controller;
    }

    private final ArrayList<CANSparkMax> followers;

    public ArrayList<CANSparkMax> getFollowers() {
        return followers;
    }

    public TorqueNEO(final int id) {
        motor = new CANSparkMax(id, MotorType.kBrushless);
        encoder = motor.getEncoder();
        controller = motor.getPIDController();
        followers = new ArrayList<>();
    }

    public final void addFollower(final int id, final boolean invert) {
        followers.add(new CANSparkMax(id, MotorType.kBrushless));
        followers.get(followers.size() - 1).follow(motor, invert);
    }

    public void configurePID(final TorquePID pid) {
        checkError(controller.setP(pid.getProportional()), "p term");
        checkError(controller.setI(pid.getIntegral()), "i term");
        checkError(controller.setD(pid.getDerivative()), "d term");
        checkError(controller.setIZone(pid.getIntegralZone()), "i zone");
        checkError(controller.setFF(pid.getFeedForward()), "ff term");
        checkError(controller.setOutputRange(pid.getMinOutput(), pid.getMaxOutput()), "output range");
    }

    private void checkError(final REVLibError error) {
        checkError(error, "N/A");
    }

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

    /**
     * Collected representation of the SmartMotionProfile parameters.
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

    public void configureSmartMotion(final SmartMotionProfile profile) {
        checkError(controller.setSmartMotionMaxVelocity(profile.maxVelocity, profile.slot), "max velocity");
        checkError(controller.setSmartMotionMinOutputVelocity(profile.minVelocity, profile.slot), "min velocity");
        checkError(controller.setSmartMotionMaxAccel(profile.maxAcceleration, profile.slot), "max acceleration");
        checkError(controller.setSmartMotionAllowedClosedLoopError(profile.allowedError, profile.slot), "allowed error");
    }

    public void setPercent(final double percent) {
        motor.set(percent);
    }

    public double getPercent() {
        return motor.getAppliedOutput();
    }

    public void setVolts(final double volts) {
        motor.setVoltage(volts);
    }

    public double getVolts() {
        return motor.getBusVoltage();
    }

    public void setCurrent(final double current) {
        checkError(controller.setReference(current, ControlType.kCurrent));
    }

    public double getCurrent() {
        return motor.getOutputCurrent();
    }

    public void setCurrentLimit(final int amps) {
        checkError(motor.setSmartCurrentLimit(amps));
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

}
