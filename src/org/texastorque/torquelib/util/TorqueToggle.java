package org.texastorque.torquelib.util;

public class TorqueToggle
{
    
    private boolean toggle;
    private boolean lastCheck;
    
    public TorqueToggle() 
    {
        toggle = false;
        lastCheck = false;
    }

    public void calc(boolean current)
    {
        if(current!=lastCheck)
        {
            if(!current)
            {
                lastCheck = false;
            }//if button release
            else
            {
                lastCheck = true;
                toggle = !toggle;
            }//if button press
        }//if event 
    }//update the toggle
    
    public void set(boolean override)
    {
        toggle = override;
    }
    
    public boolean get()
    {
        return toggle;
    }
}
