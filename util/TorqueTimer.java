package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;

public class TorqueTimer {

	private double startTime = -1;
	private double lastTime = -1;
	private boolean started = false;
	
	public TorqueTimer() { }
	
	public double elapsed() {
		startIfNeeded();
		
		return Timer.getFPGATimestamp() - this.startTime;
	}
	
	public double lapTime() {
		startIfNeeded();
		
		return Timer.getFPGATimestamp() - this.lastTime;
	}
	
	public void startLap() {
		this.lastTime = Timer.getFPGATimestamp();
	}
	
	public double timeSince(double lastTime) {
		startIfNeeded();
		
		return Timer.getFPGATimestamp() - lastTime;
	}
	
	public double start() {
		if (this.started) {
			return this.startTime;
		}
		
		this.startTime = Timer.getFPGATimestamp();
		this.lastTime = this.startTime;
		
		return this.startTime;
	}
	
	public boolean isRunning() {
		return this.started;
	}
	
	public void reset() {
		this.started = false;
	}
	
	private void startIfNeeded() {
		if (this.startTime <= 0 || this.lastTime <= 0) {
			start();
			this.started = true;
		}
	}
}