package org.texastorque.torquelib.controlLoop;

public class TrapezoidalProfile {

    double m_currentAccel;
    double m_currentVel;
    double m_currentPos;

    double m_maxSpeed;
    double m_maxAccel;

    private class AccelerationProfile {

        // Java initializes all of these to zero automatically

        public double startAccel;
        public double startTime;
        public double constTime;
        public double endAccel;
        public double endTime;
    }

    public TrapezoidalProfile(double maxAccel, double maxSpeed) {
        m_maxAccel = maxAccel;
        m_maxSpeed = maxSpeed;
    }

    public double getVelocity() {
        return m_currentVel;
    }

    public double getAcceleration() {
        return m_currentAccel;
    }

    public double getPosition() {
        return m_currentPos;
    }

    public void reset() {
        m_currentAccel = m_currentVel = m_currentPos = 0.0;
    }

    private void updateKinematics(double accel, double dt) {
        m_currentAccel = accel;
        m_currentPos += m_currentVel * dt + .5 * m_currentAccel * dt * dt;
        m_currentVel += m_currentAccel * dt;
    }

    private AccelerationProfile generateAccelerationProfile(double actualDistance, double actualSpeed, double goalSpeed) {
        AccelerationProfile profile = new AccelerationProfile();
        // Figure out the sign of the acceleration at the end of the trapezoid
        double start_a = 0.0;
        if (actualDistance > 0.0) {
            start_a = m_maxAccel;
        } else if (actualDistance == 0.0) {
            return profile;
        } else {
            profile = generateAccelerationProfile(-actualDistance, -actualSpeed, -goalSpeed);
            profile.startAccel *= -1;
            profile.endAccel *= -1;
            return profile;
        }

        // If we floored it the entire way, how fast would we go?
        // vf^2 = v0^2 + 2*a*d
        // vf = sqrt(v0^2 + 2*a*d)
        double maxAchievableVelocity = Math.sqrt(actualSpeed * actualSpeed + 2.0 * actualDistance * Math.abs(start_a));

        double final_a;
        double maxGoalVelocity; // The largest velocity to command to hit the goal
        if (maxAchievableVelocity >= goalSpeed) {
            final_a = -m_maxAccel;

            // Find the top speed we can get to while then decelerating to our goal speed
            // Here are the equations:
            //   vmax^2 = actualSpeed^2 + 2*start_a*accelDistance (acceleration)
            //   vgoal^2 = vmax^2 + 2*final_a*decelDistance (deceleration)
            //   actualDistance = accelDistance + decelDistance (total distance)
            // By combining and solving for vmax, we obtain:
            //   accelDistance = (vmax^2 - actualSpeed^2)/(2*start_a)
            //   decelDistance = (vgoal^2 - vmax^2)/(2*final_a)
            //   actualDistance = (vmax^2 - actualSpeed^2)/(2*start_a) + (vgoal^2 - vmax^2)/(2*final_a)
            //   actualDistance = vmax^2/(2*start_a) - actualSpeed^2/(2*start_a) + vgoal^2/(2*final_a) - vmax^2/(2*final_a)
            //   vmax^2/(2*final_a) - vmax^2/(2*start_a) = -actualDistance - actualSpeed^2/(2*start_a) + vgoal^2/(2*final_a)
            //   vmax^2*(1/(2*final_a)-1/(2*start_a)) = -actualDistance - actualSpeed^2/(2*start_a) + vgoal^2/(2*final_a)
            //   vmax = sqrt( (-actualDistance - actualSpeed^2/(2*start_a) + vgoal^2/(2*final_a)) / (1/(2*final_a)-1/(2*start_a)) )
            maxGoalVelocity = Math.sqrt((-actualDistance - (actualSpeed * actualSpeed) / (2.0 * start_a) + (goalSpeed * goalSpeed) / (2.0 * final_a)) / (1.0 / (2.0 * final_a) - 1.0 / (2.0 * start_a)));
        } else {
            final_a = m_maxAccel;
            maxGoalVelocity = maxAchievableVelocity;
        }

        double accel_time = 0;
        double const_time = 0;
        // If we can get to a higher speed than allowed, then cap the speed
        if (maxGoalVelocity > m_maxSpeed) {
            // The acceleration time is simply how long it takes to reach this speed
            accel_time = (m_maxSpeed - actualSpeed) / m_maxAccel;

            // The constant speed time is found by:
            //   constTime = constDistance / vmax
            //   constTime = (actualDistance - accelDistance - decelDistance) / vmax
            const_time = (actualDistance - (goalSpeed * goalSpeed - m_maxSpeed * m_maxSpeed) / (2.0 * final_a) - (m_maxSpeed * m_maxSpeed - actualSpeed * actualSpeed) / (2.0 * start_a)) / m_maxSpeed;
        } else {
            // The acceleration time is simple how long it takes to reach the top speed
            accel_time = (maxGoalVelocity - actualSpeed) / start_a;
        }

        profile.startTime = Math.max(accel_time, 0.0);
        profile.startAccel = start_a;
        profile.constTime = Math.max(const_time, 0.0);
        // The deceleration time is simply the time it takes to decelerate from top to goal speed
        profile.endTime = Math.max((goalSpeed - maxGoalVelocity) / final_a, 0.0);
        profile.endAccel = final_a;

        return profile;
    }

    public void update(double actualDistance, double actualSpeed, double goalSpeed, double dt) {
        AccelerationProfile profile = generateAccelerationProfile(actualDistance, actualSpeed, goalSpeed);

        double timeLeft = dt;
        if (profile.startTime > timeLeft) {
            updateKinematics(profile.startAccel, timeLeft);
        } else {
            updateKinematics(profile.startAccel, profile.startTime);
            timeLeft -= profile.startTime;

            if (profile.constTime > timeLeft) {
                updateKinematics(0.0, timeLeft);
            } else {
                updateKinematics(0.0, profile.constTime);
                timeLeft -= profile.constTime;

                if (profile.endTime > timeLeft) {
                    updateKinematics(profile.endAccel, timeLeft);
                } else {
                    updateKinematics(profile.endAccel, profile.endTime);
                    timeLeft -= profile.endTime;
                    updateKinematics(0, timeLeft);
                }
            }
        }
    }
}
