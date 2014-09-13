package org.texastorque.torquelib.controlLoop;

public/* abstract*/ class TorquePIDThread extends Thread
{
    private double Kp, Ki, Td;
    private double epsilon/*, sigma, psi*/;
    private double cumulativeError;
    protected double instantaneousError;
    private double previousError;
    private double output;
    public double currentPosition;
    private double setpoint;
    private long threadInterval;
    private boolean isRunning;
    private boolean waiting;
    
    public TorquePIDThread()
    {
        Kp = Ki = Td = epsilon/* = sigma = psi*/ = 0.0;
        cumulativeError = instantaneousError = previousError = 0.0;
        output = currentPosition = setpoint = 0.0;
        threadInterval = 0;
        isRunning = waiting = false;
    }
    
    public void setSetpoint(double setpt)
    {
        setpoint = setpt;
        cumulativeError = 0.0;
    }
    
    public double getSetpoint()
    {
        return setpoint;
    }
    
    public void setPIDConstants(double p, double i, double d)
    {
        Kp = p;
        Ki = i;
        Td = d;
        if(Kp < 0)
        {
            Kp = 0;
        }
        if(Ki < 0)
        {
            Ki = 0;
        }
        if(Td < 0)
        {
            Td = 0;
        }
    }
    
    public boolean getIsRunning()
    {
        return isRunning;
    }
    
    public void setThreadInterval(long threadInt)
    {
        threadInterval = threadInt;
    }
    
    public void setOptions(double e/*, double s, double p*/)
    {
        epsilon = e;
        //sigma = s;
        //psi = p;
    }
    
    public double getOutput()
    {
        return output;
    }
    
    //public abstract void setCurrentPosition();
    
    public void setCurrentPosition(double currPosition)
    {
        this.currentPosition = currPosition;
    }
    
    //public abstract double calcCee();
    
    private void calcP()
    {
        previousError = instantaneousError;
        instantaneousError = (setpoint - currentPosition);
    }
    
    private double calcI()
    {
        if(Math.abs(instantaneousError) < epsilon && Ki != 0.0)
        {
           if(currentPosition > setpoint * 0.99 && currentPosition < setpoint * 1.01)
           {
               cumulativeError = 0.0;
               return 0.0;
           }
           cumulativeError += instantaneousError;
           double tempI = cumulativeError * Ki;
           /*if(Math.abs(tempI) < sigma)
           {
               return tempI;
           }*/
           return tempI;
        }
        cumulativeError = 0.0;
        return 0.0;
    }
    
    private double calcD()
    {
        //if(Math.abs(instantaneousError) < psi && threadInterval != 0)
        //{
        
        if(threadInterval != 0)
        {   
            return Td * (double)((instantaneousError - previousError) / (double)(threadInterval / 1000.0));
        }
        return 0.0;
        //}
        //return 0.0;
    }
    
    private double calcPID()
    {
        calcP();
        //double tempOutput = calcCee();
        double tempOutput = 0.0;
        tempOutput = (Kp * instantaneousError) + calcI() + calcD();
        return tempOutput;
    }
    
    public void start()
    {
        if(!isRunning)
        {
            isRunning = true;
            super.start();
        }
        else
        {
            waiting = false;
        }
        cumulativeError = 0.0;
    }
    
    public void run()
    {
        while(isRunning)
        {
            while(waiting){}
            //setCurrentPosition();
            output = calcPID();
            System.err.println("Position " + currentPosition + "\tOutput: " + output + "\tIE: " + instantaneousError);
            try{super.sleep(threadInterval);}catch(InterruptedException e){}
        }
        isRunning = false;
    }
}