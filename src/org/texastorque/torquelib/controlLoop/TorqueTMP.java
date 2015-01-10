package org.texastorque.torquelib.controlLoop;

/**
 * Created by Gijs on 12/31/2014.
 */
public class TorqueTMP {

    //Current value

    private double currentPosition;
    private double currentVelocity;
    private double currentAcceleration;

    //Path generation parameters
    private double maxAllowedVelocity;
    private double maxAllowedAcceleration;

    //Path properties
    private double acceleration;
    private double accelerationTime;
    private double topSpeed;
    private double cruiseTime;
    private double deceleration;
    private double decelerationTime;

    public TorqueTMP(double maxV, double maxA) {
        maxAllowedVelocity = maxV;
        maxAllowedAcceleration = maxA;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public double getCurrentVelocity() {
        return currentVelocity;
    }

    public double getCurrentAcceleration() {
        return currentAcceleration;
    }

    public void generateTrapezoid(double targetPosition, double realPosition, double realSpeed) {

        double positionError = targetPosition - realPosition;

        System.out.println(positionError);

        if (Math.abs(positionError) < 0.1) {
            return;
        } else if (positionError < 0.0) {
            generateTrapezoid(-targetPosition, -realPosition, -realSpeed);
            acceleration *= -1;
            deceleration *= -1;
            topSpeed *= -1;
            currentVelocity *= -1;
            return;
        }

        //This is the maximum speed we can get up and slow down to a stop from given our situation and target situation.
        //We are assuming our final speed is 0. In practice this means we can only use this clas for situations where we
        //want to move from one position to another and stop. It lets us simplify our math a little bit, and we do not currently
        //have a situation that does not meet those criteria.
        //
        // Vmax^2 = Vnow^2 + 2 * acceleration * accelerationDistance
        // 0^2 = Vmax^2 + 2 * deceleration * decelerationDistance
        // acceleration = -1 * deceleration
        //
        // accelerationDistance = (Vmax^2 - Vnow^2) / 2 * acceleration
        // decelerationDistance = (-Vmax^2) / (2 * -1 * deceleration)
        // decelerationDistance = Vmax^2 / (2 * acceleration)
        //
        // Total distance = x >= accelerationDistance + decelerationDistance
        // x = ((Vmax^2 - Vnow^2) / 2 * acceleration) + (Vmax^2 / (2 * acceleration)
        // x = (2Vmax^2 - Vnow^2) / (2 * acceleration)
        // x * 2 * acceleration = 2Vmax^2 - Vnow^2
        // 2Vmax^2 = 2 * x * acceleration + Vnow^2
        // Vmax = sqrt( (2 * x * acceleration + Vnow^2) / 2 )
        double maximumPossibleSpeed = Math.sqrt((2 * maxAllowedAcceleration * positionError + realSpeed * realSpeed) / 2);
        //Limit the max speed if it is higher than we want the system to ever move.
        topSpeed = Math.min(maximumPossibleSpeed, maxAllowedVelocity);

        //Calculate the time we will spend accelerating.
        acceleration = maxAllowedAcceleration;
        accelerationTime = Math.max(((topSpeed - realSpeed) / acceleration), 0.0);
        //Vf^2 = V^2 + 2 * a * dX
        //Vf^2 - v^2 = 2 * a * dX
        //dX = (Vf^2 - V^2) / (2 * a)
        double accelerationDistance = (topSpeed * topSpeed - realSpeed * realSpeed) / (2 * acceleration);

        deceleration = -1 * acceleration;
        decelerationTime = (0 - topSpeed) / deceleration;
        //Vf^2 = V^2 + 2 * a * dX
        //Vf^2 = 0 because we want to stop
        //-v^2 = 2 * a * dX
        //dX = (-V^2) / (2 * a)
        double decelerationDistance = Math.max((-1 * (topSpeed * topSpeed) / (2 * deceleration)), 0.0);

        //Cruising distance is the distance we do not spend accelerating or decelerating.
        double cruiseDistance = positionError - accelerationDistance - decelerationDistance;
        //Cruise time is cruising distance divided by the speed at which we cruise.
        cruiseTime = cruiseDistance / topSpeed;

        //Our target velocity right now is our current velocity because thats what
        //the profile was based on.
        currentVelocity = realSpeed;

        //Our target position right now is our current position because thats what
        //the profile was based on.
        currentPosition = realPosition;
    }

    /**
     * Calculate what our position, velocity, and acceleration should be in the
     * future.
     *
     *
     * @param dt
     */
    public void calculateNextSituation(double dt) {
        if (dt < accelerationTime) {
            accelerate(dt);
        } else if (dt < accelerationTime + cruiseTime) {
            accelerate(accelerationTime);
            cruise(dt - accelerationTime);
        } else if (dt < accelerationTime + cruiseTime + decelerationTime) {
            accelerate(accelerationTime);
            cruise(cruiseTime);
            decelerate(dt - accelerationTime - cruiseTime);
        } else {
            accelerate(accelerationTime);
            cruise(cruiseTime);
            decelerate(decelerationTime);
            currentAcceleration = 0.0;
        }
    }

    /**
     * Accelerate at maximum acceleration for the specified amount of time.
     *
     * @param dt The time to accelerate for.
     */
    private void accelerate(double dt) {
        currentAcceleration = acceleration;
        currentPosition += currentVelocity * dt + 0.5 * currentAcceleration * dt * dt;
        currentVelocity += acceleration * dt;
    }

    /**
     * Cruise for the specified amount of time.
     *
     * @param dt The time to cruise for.
     */
    private void cruise(double dt) {
        currentAcceleration = 0.0;
        currentPosition += topSpeed * dt;
        currentVelocity = topSpeed;
    }

    /**
     * Decelerate at -1 * maximum acceleration for the specified amount of time.
     *
     * @param dt The time to decelerate for.
     */
    private void decelerate(double dt) {
        currentAcceleration = deceleration;
        currentPosition += currentVelocity * dt + 0.5 * deceleration * dt * dt;
        currentVelocity += deceleration * dt;
    }

    public String toString() {
        System.out.println("P: " + getCurrentPosition());
        System.out.println("V: " + getCurrentVelocity());
        System.out.println("A: " + getCurrentAcceleration());
        return "";
    }
}
