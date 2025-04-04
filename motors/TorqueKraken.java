package org.texastorque.torquelib.motors;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class TorqueKraken {

	private final TalonFX motor;
	private final TalonFXConfiguration config;

	private final PositionVoltage positionVoltage = new PositionVoltage(0).withSlot(0);

	public TorqueKraken(final int port) {
		motor = new TalonFX(port);
		config = new TalonFXConfiguration();
	}

	public TorqueKraken inverted(final boolean invert) {
		config.MotorOutput.Inverted = invert ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
		return this;
	}

	public TorqueKraken idleMode(final NeutralModeValue idleMode) {
		config.MotorOutput.NeutralMode = idleMode;
		return this;
	}

	public TorqueKraken pid(final double p, final double i, final double d) {
		config.Slot0.kP = p;
		config.Slot0.kI = i;
		config.Slot0.kD = d;
		return this;
	}

	public TorqueKraken trapezoidal(final double maxV, final double maxA) {
		config.Slot0.kV = maxV;
		config.Slot0.kA = maxA;
		config.MotionMagic.MotionMagicAcceleration = maxA;
		config.MotionMagic.MotionMagicCruiseVelocity = maxV;
		return this;
	}

	public TorqueKraken currentLimit(final double currentLimit) {
		config.CurrentLimits.StatorCurrentLimitEnable = true;
		config.CurrentLimits.StatorCurrentLimit = currentLimit;
		return this;
	}

	public TorqueKraken apply() {
		motor.getConfigurator().apply(config);
		return this;
	}

	public void setPercent(final double percent) {
		motor.set(percent);
	}

	public double getPercent() {
		return motor.get();
	}

	public void setDesiredPosition(final double position) {
		motor.setControl(positionVoltage.withPosition(position));
	}

	public void setVolts(final double volts) {
		motor.setVoltage(volts);
	}

	public double getVolts() {
		return motor.getMotorVoltage().getValueAsDouble();
	}

	public double getOutputCurrent() {
		return motor.getStatorCurrent().getValueAsDouble();
	}

	public double getPosition() {
		return motor.getPosition().getValueAsDouble();
	}

	public double getVelocity() {
		return motor.getVelocity().getValueAsDouble();
	}

	public void setPosition(final double position) {
		motor.setPosition(position);
	}
}
