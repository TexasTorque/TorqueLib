package org.texastorque.torquelib.control;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;

public class TorqueFieldZone {

	private final String name;
	private final Translation2d start, end;

	public TorqueFieldZone(String name, Translation2d start, Translation2d end) {
		this.name = name;
		this.start = start;
		this.end = end;
	}

	// Returns whether pose is contained between start (inclusive) and end (exclusive)
	public boolean contains(final Pose2d pose) {
		final boolean x = pose.getX() >= start.getX() && pose.getX() < end.getX();
		final boolean y = pose.getY() >= start.getY() && pose.getY() < end.getY();
		return x && y;
	}

	public String getName() {
	  return name;
	}
}
