package org.texastorque.torquelib.controlLoop;

public class TorquePV extends ControlLoop {
    
    private double kP;
    private double kV;
    private double kFFV;
    private double kFFA;
    
    private double integral;
 
    public TorquePV() {
        super();
        
        kP = 0.0;
        kV = 0.0;
        kFFV = 0.0;
        kFFA = 0.0;
    }
    
    public double calculate(TorqueTMP tmProfile, double currentPosition, double currentVelocity) {
        setPoint = tmProfile.getCurrentVelocity();
        currentValue = currentVelocity;
        
        double output = 0.0;
        
        //Position P
        double error  = tmProfile.getCurrentPosition() - currentPosition;
        output += (error * kP);

        //Velocity P
        double velocityError = tmProfile.getCurrentVelocity() - currentVelocity;
        output += (velocityError * kV);
        
        //Velocity FeedForward
        output += (tmProfile.getCurrentVelocity() * kFFV);
        
        //Acceleration FeedForward
        output += (tmProfile.getCurrentAcceleration() * kFFA);
        
        return output;
    }
    
    public void setGains(double p, double v, double ffV, double ffA) {
        kP = p;
        kV = v;
        kFFV = ffV;
        kFFA = ffA;
    }
    
    public void reset() {
        integral = 0.0;
    }
}
