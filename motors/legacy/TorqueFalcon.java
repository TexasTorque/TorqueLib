/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.motors.legacy;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import java.util.ArrayList;
import org.texastorque.torquelib.control.TorquePID;
import org.texastorque.torquelib.motors.legacy.base.TorqueMotor;
import org.texastorque.torquelib.motors.legacy.base.TorqueSmartMotor;
import org.texastorque.torquelib.util.KPID;

// TODO Peep this: https://github.com/Team364/BaseFalconSwerve/blob/main/src/main/java/frc/lib/math/Conversions.java

/**
 * The Texas Torque wrapper for the Falcon (TalonFX) motor controller.
 *
 * @author Justus Languell
 * @author Jack Pittenger
 * 
 * @deprecated NOT SUITABLE FOR 2023
 */
@Deprecated
public final class TorqueFalcon extends TorqueMotor implements TorqueSmartMotor {
    private WPI_TalonFX motor;
    private ArrayList<WPI_TalonFX> followers = new ArrayList<>();

    /**
     * Clicks per rotation on the motor encoder.
     */
    public final double CLICKS_PER_ROTATION = 4096;

    private double lastVelocity;
    private long lastVelocityTime;

    private NeutralMode neutralMode;
    private final TalonFXConfiguration config;

    /**
     * Construct a new TorqueFalcon motor.
     *
     * @param port The port (ID) of the motor.
     */
    public TorqueFalcon(final int port) {
        super(port);

        motor = new WPI_TalonFX(port);

        neutralMode = NeutralMode.EEPROMSetting;
        motor.setNeutralMode(neutralMode);

        config = new TalonFXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;
        motor.configAllSettings(config);

        this.lastVelocity = 0;
        this.lastVelocityTime = System.currentTimeMillis();
    }

    /**
     * Add a follower Falcon.
     *
     * @param port The port (ID) of the follower Falcon.
     */
    @Override
    public final void addFollower(final int port) {
        WPI_TalonFX follower = new WPI_TalonFX(port);
        follower.setNeutralMode(neutralMode);
        follower.follow(motor);
        followers.add(follower);
    }

    /**
     * Add a follower Falcon and optionally invert.
     *
     * @param port The port (ID) of the follower Falcon.
     */
    @Override
    public final void addFollower(final int port, final boolean invert) {
        WPI_TalonFX follower = new WPI_TalonFX(port);
        follower.setNeutralMode(neutralMode);
        follower.setInverted(invert);
        follower.follow(motor);
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
     * Configures the PID controller for the motor.
     *
     * @param kPID The KPID value to configure the motor too.
     */
    @Override
    @Deprecated
    public final void configurePID(final KPID kPID) {
        motor.config_kP(0, kPID.getPGains());
        motor.config_kI(0, kPID.getIGains());
        motor.config_kD(0, kPID.getDGains());
        motor.config_kF(0, kPID.getFGains());
        if (kPID.getIZone() > 0) motor.config_IntegralZone(0, kPID.getIZone());
        motor.configPeakOutputForward(kPID.getMax());
        motor.configPeakOutputReverse(kPID.getMin());
    }

    /**
     * Configures the PID controller for the motor.
     *
     * @param pid The PID to configure the motor with.
     */
    @Override
    public final void configurePID(final TorquePID pid) {
        motor.config_kP(0, pid.getProportional());
        motor.config_kI(0, pid.getIntegral());
        motor.config_kD(0, pid.getDerivative());
        motor.config_kF(0, pid.getFeedForward());
        if (pid.hasIntegralZone()) motor.config_IntegralZone(0, pid.getIntegralZone());
        motor.configPeakOutputForward(pid.getMaxOutput());
        motor.configPeakOutputReverse(pid.getMinOutput());
    }

    /**
     * Sets the output of the motor to the given percent.
     *
     * @param percent The percent the motor should output at.
     */
    @Override
    public final void setPercent(final double percent) {
        motor.set(ControlMode.PercentOutput, percent);
        for (WPI_TalonFX follower : followers) follower.set(ControlMode.Follower, port);
    }

    /**
     * Set the motor to output a certain voltage setpoint.
     *
     * @param outputVolts The voltage to output.
     */
    @Override
    public final void setVoltage(final double outputVolts) {
        motor.setVoltage(outputVolts);
        for (WPI_TalonFX follower : followers) follower.set(ControlMode.Follower, port);
    }

    // Setters implemented from TorquePIDMotor

    /**
     * Set the motor's position in encoder units.
     *
     * @param setpoint The encoder units to set the motor to.
     */
    @Override
    public final void setPosition(final double setpoint) {
        motor.set(ControlMode.Position, setpoint);
        for (WPI_TalonFX follower : followers) follower.set(ControlMode.Follower, port);
    }

    /**
     * Set the motor's position in degrees.
     *
     * @param setpoint The degrees to set the motor to.
     */
    @Override
    public final void setPositionDegrees(final double setpoint) {
        setPositionRotations(setpoint / 360);
    }

    /**
     * Set the motor's position in rotations.
     *
     * @param setpoint The rotations to set the motor to.
     */
    @Override
    public final void setPositionRotations(final double setpoint) {
        setPosition(setpoint * CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's velocity in encoder units per second.
     *
     * @param setpoint The encoder units per second to set the motor to.
     */
    @Override
    public final void setVelocity(final double setpoint) {
        motor.set(ControlMode.Velocity, setpoint / 10);
        for (WPI_TalonFX follower : followers) follower.set(ControlMode.Follower, port);
    }

    /**
     * Set the motor's velocity in RPS.
     *
     * @param setpoint The RPS to set the motor to.
     */
    @Override
    public final void setVelocityRPS(final double setpoint) {
        setVelocity(setpoint * CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's velocity in RPM.
     *
     * @param setpoint The RPM to set the motor to.
     */
    @Override
    public final void setVelocityRPM(final double setpoint) {
        setVelocityRPS(setpoint / 60);
    }

    // Getters implemented from TorqueEncoderMotor

    /**
     * Get the position of the motor in encoder units.
     *
     * @return The position of the encoder in encoder units.
     */
    @Override
    public final double getPosition() {
        return motor.getSelectedSensorPosition();
    }

    /**
     * Get the position of the motor in degrees.
     *
     * @return The position of the encoder in degrees.
     */
    @Override
    public final double getPositionDegrees() {
        return getPositionRotations() * 360;
    }

    /**
     * Get the position of the motor in rotations.
     *
     * @return The position of the encoder in rotations.
     */
    @Override
    public final double getPositionRotations() {
        return getPosition() / CLICKS_PER_ROTATION;
    }

    /**
     * Get the velocity of the motor in encoder units per second.
     *
     * @return acceleration in encoder units per second.
     */
    @Override
    public final double getVelocity() {
        return motor.getSelectedSensorVelocity() * 10;
    }

    /**
     * Get the velocity of the motor in RPS.
     *
     * @return acceleration in RPS.
     */
    @Override
    public final double getVelocityRPS() {
        return getVelocity() / CLICKS_PER_ROTATION;
    }

    /**
     * Get the velocity of the motor in RPM.
     *
     * @return acceleration in RPM.
     */
    @Override
    public final double getVelocityRPM() {
        return getVelocityRPS() * 60;
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

    /**
     * Set max amps supply.
     *
     * @param limit Supply limit config.
     */
    public final void setSupplyLimit(final SupplyCurrentLimitConfiguration limit) {
        ErrorCode e = motor.configSupplyCurrentLimit(limit);
        if (e != ErrorCode.OK)
            System.out.printf("TorqueFalcon port %d: Error configuring supply limit: %s\n", port, e.name());
    }

    /**
     * Set max amps supply.
     *
     * @param limit Stator limit config.
     */
    public final void setStatorLimit(final StatorCurrentLimitConfiguration limit) {
        ErrorCode e = motor.configStatorCurrentLimit(limit);
        if (e != ErrorCode.OK)
            System.out.printf("TorqueFalcon port %d: Error configuring supply limit: %s\n", port, e.name());
    }

    /**
     * Gets current used by the Falcon.
     *
     * @return The current used by the Falcon.
     */
    public final double getCurrent() { return motor.getStatorCurrent(); }

    /**
     * Zero the encoder.
     */
    public final void zeroEncoder() { motor.setSelectedSensorPosition(0); }

    /**
     * Set neutral mode.
     *
     * @param neutralMode Neutral mode.
     */
    public final void setNeutralMode(final NeutralMode neutralMode) {
        motor.setNeutralMode(neutralMode);
        for (WPI_TalonFX follower : followers) follower.setNeutralMode(neutralMode);
        this.neutralMode = neutralMode;
    }
}