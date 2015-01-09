package org.texastorque.controlloop;

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

    public void generateTrapezoid(double distance, double realSpeed) {

        if (distance == 0) {
            return;
        } else if (distance < 0.0) {
            generateTrapezoid(-distance, -realSpeed);
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
        double maximumPossibleSpeed = Math.sqrt((2 * maxAllowedAcceleration * distance + realSpeed * realSpeed) / 2);
        //Limit the max speed if it is higher than we want the system to ever move.
        topSpeed = Math.min(maximumPossibleSpeed, maxAllowedVelocity);

        System.out.println(topSpeed);

        //Calculate the time we will spend accelerating.
        acceleration = maxAllowedAcceleration;
        accelerationTime = (topSpeed - realSpeed) / acceleration;
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
        double decelerationDistance = -1 * (topSpeed * topSpeed) / (2 * deceleration);

        //Cruising distance is the distance we do not spend accelerating or decelerating.
        double cruiseDistance = distance - accelerationDistance - decelerationDistance;
        //Cruise time is cruising distance divided by the speed at which we cruise.
        cruiseTime = cruiseDistance / topSpeed;

        currentVelocity = realSpeed;
    }

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

    private void accelerate(double dt) {
        currentAcceleration = acceleration;
        currentPosition += currentVelocity * dt + 0.5 * currentAcceleration * dt * dt;
        currentVelocity += acceleration * dt;
    }

    private void cruise(double dt) {
        currentAcceleration = 0.0;
        currentPosition += topSpeed * dt;
        currentVelocity = topSpeed;
    }

    private void decelerate(double dt) {
        currentAcceleration = deceleration;
        currentPosition += currentVelocity * dt + 0.5 * deceleration * dt * dt;
        currentVelocity += deceleration * dt;
    }

    public String toString(){
        System.out.println("P: " + getCurrentPosition());
        System.out.println("V: " + getCurrentVelocity());
        System.out.println("A: " + getCurrentAcceleration());
        return "";
    }
}
