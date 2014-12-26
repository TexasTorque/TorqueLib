package org.texastorque.torquelib.controlLoop;

public class BangBang extends ControlLoop {
    
    public BangBang()
    {
        super();
    }
    
    @Override
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
}
