package org.texastorque.torquelib.control;

public final class TorqueLogisticRegression {
   
    private final double MAX_SPEED;
    private final double MAX_DISTANCE;
    private final double GROWTH;

    public TorqueLogisticRegression(final double maxSpeed, final double maxDistance, final double growth) {
        this.MAX_DISTANCE = maxDistance;
        this.MAX_SPEED = maxSpeed;
        this.GROWTH = growth;
    }

    private double z(final double error) {
        return MAX_SPEED / (Math.pow(1 + Math.E, -GROWTH * (error - MAX_DISTANCE / 2.0)));
    }

    public double calculate(final double current, final double requested) {
        final double error = current - requested;
        return (z(error) - z(0)) * Math.signum(error);

    }

 
}
