package org.texastorque.torquelib.motors;

import java.util.ArrayList;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.texastorque.torquelib.motors.base.TorqueEncoderMotor;
import org.texastorque.torquelib.motors.base.TorqueMotor;
import org.texastorque.torquelib.motors.base.TorquePIDMotor;
import org.texastorque.torquelib.motors.base.TorqueSmartMotor;
import org.texastorque.torquelib.util.KPID;

/**
 * The Texas Torque wrapper for the Talon (SRX) motor controller.
 * 
 * @author Justus Languell
 * @author Jack Pittenger
 */
public final class TorqueTalon extends TorqueMotor implements TorqueSmartMotor {
    private WPI_TalonSRX motor;
    private ArrayList<WPI_TalonSRX> followers = new ArrayList<>();

    public final double CLICKS_PER_ROTATION = 4096;

    private double lastVelocity;
    private long lastVelocityTime;

    /**
     * Construct a new TorqueTalon motor.
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
     * Add a follower Talon.
     * 
     * @param port The port (ID) of the follower Talon.
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
        if (kPID.getIZone() > 0)
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

    /**
     * Set the motor to output a certain voltage setpoint.
     * 
     * @param setpoint The voltage to output.
     */
    @Override
    public void setVoltage(final double outputVolts) {
        motor.setVoltage(outputVolts);
        for (WPI_TalonSRX follower : followers)
            follower.set(ControlMode.Follower, port);
    }

    // Setters implemented from TorquePIDMotor

    /**
     * Set the motor's position in encoder units.
     * 
     * @param setpoint The encoder units to set the motor to.
     */
    @Override
    public void setPosition(final double setpoint) {
        motor.set(ControlMode.Position, setpoint);
        for (WPI_TalonSRX follower : followers)
            follower.set(ControlMode.Follower, port);
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
        setPosition(setpoint * CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's velocity in encoder units per second.
     * 
     * @param setpoint The encoder units per second to set the motor to.
     */
    @Override
    public void setVelocity(final double setpoint) {
        motor.set(ControlMode.Velocity, setpoint / 10);
        for (WPI_TalonSRX follower : followers)
            follower.set(ControlMode.Follower, port); 
    }

    /**
     * Set the motor's velocity in RPS.
     * 
     * @param setpoint The RPS to set the motor to.
     */
    @Override
    public void setVelocityRPS(final double setpoint) {
        setVelocity(setpoint * CLICKS_PER_ROTATION);
    }

    /**
     * Set the motor's velocity in RPM.
     * 
     * @param setpoint The RPM to set the motor to.
     */
    @Override
    public void setVelocityRPM(final double setpoint) {
        setVelocityRPS(setpoint / 60);
    }

    // Getters implemented from TorqueEncoderMotor

    /**
     * Get the position of the motor in encoder units.
     *
     * @return The position of the encoder in encoder units.
     */
    @Override
    public double getPosition() {
        return motor.getSelectedSensorPosition();
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
        return getPosition() / CLICKS_PER_ROTATION;
    }

    /**
     * Get the velocity of the motor in encoder units per second.
     * 
     * @return acceleration in encoder units per second.
     */
    @Override
    public double getVelocity() {
        return motor.getSelectedSensorVelocity() * 10;

    }

    /**
     * Get the velocity of the motor in RPS.
     * 
     * @return acceleration in RPS.
     */
    @Override
    public double getVelocityRPS() {
        return getVelocity() / CLICKS_PER_ROTATION;

    }

    /**
     * Get the velocity of the motor in RPM.
     * 
     * @return acceleration in RPM.
     */
    @Override
    public double getVelocityRPM() {
        return getVelocityRPS() * 60;
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

    /**
     * Set max amps supply.
     * 
     * @param limit Max amps.
     */
    public void setSupplyLimit(final SupplyCurrentLimitConfiguration limit) {
        ErrorCode e = motor.configSupplyCurrentLimit(limit);
        if (e != ErrorCode.OK)
            System.out.printf("TorqueTalon port %d: Error configuring supply limit: %s\n", port, e.name());
    }

    /**
     * Gets current used by the Talon.
     * 
     * @return The current used by the Talon.
     */
    public double getCurrent() {
        return motor.getStatorCurrent();
    }

    /**
     * Zero the encoder.
     */
    public void zeroEncoder() {
        motor.setSelectedSensorPosition(0);
    }
}