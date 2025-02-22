package org.texastorque.torquelib.motors;

import org.texastorque.torquelib.control.TorqueDebug;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

/**
 * Wrapper for the 2025 RevLib rewrite of the SparkMax
 * class and configuration library
 * 
 * @author Davey Adams
 */
public class TorqueNEO {
	
	private final SparkMax motor;
	private final SparkMaxConfig config;

	public TorqueNEO(final int port) {
		motor = new SparkMax(port, MotorType.kBrushless);
		config = new SparkMaxConfig();
	}

	public TorqueNEO inverted(final boolean inverted) {
		config.inverted(inverted);
		return this;
	}

	public TorqueNEO idleMode(final IdleMode mode) {
		config.idleMode(mode);
		return this;
	}

	public TorqueNEO pid(final double p, final double i, final double d) {
		config.closedLoop.pid(p, i, d);
		return this;
	}

	public TorqueNEO voltageCompensation(final double voltage) {
		config.voltageCompensation(voltage);
		return this;
	}

	public TorqueNEO disableVoltageCompensation() {
		config.disableVoltageCompensation();
		return this;
	}

	public TorqueNEO currentLimit(final int limit) {
		config.smartCurrentLimit(limit);
		return this;
	}

	public TorqueNEO conversionFactors(final double posFactor, final double veloFactor) {
		config.encoder.positionConversionFactor(posFactor);
		config.encoder.velocityConversionFactor(veloFactor);
		return this;
	}

	public TorqueNEO apply() {
		motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
		return this;
	}

	public TorqueNEO debug(final String name) {
        new TorqueDebug(this, name);
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
