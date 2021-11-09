package org.texastorque.torquelib.util;

public class TorqueIntegrator {
	
	private double lastValue;
	private double sum;
	
	public TorqueIntegrator() { }
	
	public double calculate(double value, double delta) {
		this.sum += TorqueMathUtil.calcAreaTrapezoid(lastValue, value, delta);
		this.lastValue = value;
		
		return this.sum;
	}
	
	public void reset() {
		this.sum = 0;
	}
}