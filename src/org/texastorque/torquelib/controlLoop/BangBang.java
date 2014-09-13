package org.texastorque.torquelib.controlLoop;

public class BangBang {
    
    private double setPoint;
    private double currentValue;
    private double doneRange;
    
    public BangBang()
    {
        setPoint = 0;
    }
    
    public void setDoneRange(double range)
    {
        doneRange = range;
    }
    
    public void setSetpoint(double set)
    {
        setPoint = set;
    }
    
    public double calculate(double current)
    {
        currentValue = current;
        if (currentValue < setPoint)
        {
            return 1.0;
        } else {
            return 0.0;
        }
    }
    
    public boolean isDone()
    {
        return Math.abs(currentValue - setPoint) < doneRange;
    }
}
