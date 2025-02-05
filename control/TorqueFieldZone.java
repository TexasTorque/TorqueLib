package org.texastorque.torquelib.control;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;

public class TorqueFieldZone {
	Translation2d[] points;
	public TorqueFieldZone(Translation2d ...points) {
		this.points = points;
	}

	// Returns whether pose is contained between start (inclusive) and end (exclusive)
	// Utilizes ray-casting algorithm
	public boolean contains(final Pose2d pose) {
		double x = pose.getX();
		double y = pose.getY();
		int count = 0;

		for(int i=0; i<points.length; i++){
			if(x == points[i].getX() || y == points[i].getY()){
				return true;
			}
		}

		for (int i=0; i<points.length; i++){
			Translation2d point1 = points[i];
			Translation2d point2 = points[(i+1)%points.length];

			if((y > Math.min(point1.getY(), point2.getY())) && (y <= Math.max(point1.getY(), point2.getY())) && (x < Math.max(point1.getX(), point2.getX()))){
				double xIntersect = (y-point1.getY())*(point2.getX()-point1.getX())/(point2.getY()-point1.getY())+point1.getX();
				if(point1.getX() == point2.getX() || x <= xIntersect){
					count++;
				}
			}
		}

		return count % 2 == 1;
	}
}
