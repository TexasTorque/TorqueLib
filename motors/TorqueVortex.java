package org.texastorque.torquelib.motors;

import org.texastorque.torquelib.control.TorqueDebug;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkFlexConfig;

/**
 * Wrapper for the 2026 RevLib rewrite of the SparkMax
 * class and configuration library
 * 
 * @author Davey Adams
 */
public class TorqueVortex {
	
	private final SparkFlex motor;
	private final SparkFlexConfig config;

	public TorqueVortex(final int port) {
		motor = new SparkFlex(port, MotorType.kBrushless);
		config = new SparkFlexConfig();
	}

	public TorqueVortex inverted(final boolean inverted) {
		config.inverted(inverted);
		return this;
	}

	public TorqueVortex idleMode(final IdleMode mode) {
		config.idleMode(mode);
		return this;
	}

	public TorqueVortex pid(final double p, final double i, final double d) {
		config.closedLoop.pid(p, i, d);
		return this;
	}

	public TorqueVortex voltageCompensation(final double voltage) {
		config.voltageCompensation(voltage);
		return this;
	}

	public TorqueVortex disableVoltageCompensation() {
		config.disableVoltageCompensation();
		return this;
	}

	public TorqueVortex currentLimit(final int limit) {
		config.smartCurrentLimit(limit);
		return this;
	}

	public TorqueVortex conversionFactors(final double posFactor, final double veloFactor) {
		config.encoder.positionConversionFactor(posFactor);
		config.encoder.velocityConversionFactor(veloFactor);
		return this;
	}

	public TorqueVortex follow (final int leadMotorCanID, final boolean invert) {
		config.follow(leadMotorCanID, invert);
		return this;
	}

	public TorqueVortex apply() {
        motor.configure(config, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
		return this;
	}

	public void setPercent(final double percent) {
		motor.set(percent);
	}

	public double getPercent() {
		return motor.get();
	}

	public void setVolts(final double volts) {
		motor.setVoltage(volts);
	}

	public double getVolts() {
		return motor.getAppliedOutput();
	}

	public double getBusVoltage() {
		return motor.getBusVoltage();
	}

	public double getOutputCurrent() {
		return motor.getOutputCurrent();
	}

	public double getPosition() {
		return motor.getEncoder().getPosition();
	}

	public double getVelocity() {
		return motor.getEncoder().getVelocity();
	}

	public void setPosition(final double position) {
		motor.getEncoder().setPosition(position);
	}
}
