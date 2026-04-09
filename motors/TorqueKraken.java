package org.texastorque.torquelib.motors;

import org.texastorque.torquelib.Debug;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class TorqueKraken {

	private final TalonFX motor;
	private final TalonFXConfiguration config;
	private final VelocityVoltage voltageRequest;

	public Orchestra orchestra;

	private final PositionVoltage positionVoltage = new PositionVoltage(0).withSlot(0);

	public TorqueKraken(final int port) {
		motor = new TalonFX(port);
		config = new TalonFXConfiguration();
		orchestra = new Orchestra();
		voltageRequest = new VelocityVoltage(0);
	}

	public TorqueKraken inverted(final boolean invert) {
		config.MotorOutput.Inverted = invert ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive;
		return this;
	}

	public TorqueKraken follow (final int port, final boolean inverted) {
		motor.setControl(new Follower(port, inverted ? MotorAlignmentValue.Aligned : MotorAlignmentValue.Opposed));
		return this;
	}

	public TorqueKraken idleMode(final NeutralModeValue idleMode) {
		config.MotorOutput.NeutralMode = idleMode;
		return this;
	}

	public TorqueKraken setPID(final double p, final double i, final double d) {
		config.Slot0.kP = p;
		config.Slot0.kI = i;
		config.Slot0.kD = d;
		return this;
	}

	public TorqueKraken setFF (final double ks, final double kv, final double ka) {
		config.Slot0.kS = ks;
		config.Slot0.kV = kv;
		config.Slot0.kA = ka;
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
		config.CurrentLimits.SupplyCurrentLimitEnable = true;
		config.CurrentLimits.SupplyCurrentLimit = currentLimit;
		return this;
	}

	public TorqueKraken statorCurrentLimit (final double currentLimit) {
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

	public void setRPM (final double rps) {
		motor.setControl(voltageRequest.withVelocity(rps));
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
		return motor.getSupplyCurrent().getValueAsDouble();
	}

	public double getStatorOutputCurrent () {
		return motor.getStatorCurrent().getValueAsDouble();
	}

	public double getPosition() {
		return motor.getPosition().getValueAsDouble();
	}

	public double getVelocity() {
		return motor.getVelocity().getValueAsDouble(); // RPS
	}

	public void initOrchestra(String fileName) {
		// File should be in deploy directory in ".chrp" format
		orchestra.addInstrument(motor);
		var status = orchestra.loadMusic(fileName);
		if (!status.isOK()) {
			System.out.println("Orchestra failing");
		}
	}

	public void runOrchestra() {
		// Motor will not run, if playing
		orchestra.play();
	}

	public void setPosition(final double position) {
		motor.setPosition(position);
	}
}
