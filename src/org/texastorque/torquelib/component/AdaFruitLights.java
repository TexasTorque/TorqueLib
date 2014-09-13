package org.texastorque.torquelib.component;

import edu.wpi.first.wpilibj.DigitalOutput;
import java.util.Vector;

/*
 * This class is used to control the ADAfruit lights.
 * A Vector of digital outputs encodes the desired state
 * in binary.
 */

public class AdaFruitLights
{
    
    private Vector outputVector;
    private int currentState;
    private int desiredState;
    
    /*
     * Vector of the digital outputs in which to encode the state.
     */
    public AdaFruitLights(Vector outputs)
    {
        outputVector = outputs;
    }
    
    /*
     * Desired state to be sent to the arduino.
     */
    public void setDesiredState(int state)
    {
        desiredState = state;
    }
    
    /*
     * Converts the state into binary and then encodes it
     * into the vector of digital outputs.
     */
    private void setState()
    {
        String byteString = Integer.toBinaryString(currentState);
        byteString = "0000" + byteString;
        for (int index = 0; index < outputVector.size(); index++)
        {
            int tempIndex = byteString.length() - 1 - index;
            char value = byteString.charAt(tempIndex);
            if (value == 48)
            {
                ((DigitalOutput)(outputVector.elementAt(index))).set(false);
            }
            else if(value == 49)
            {
                ((DigitalOutput)(outputVector.elementAt(index))).set(true);
            }
        }
    }
    
    /*
     * This is called continually to manage the signal to the arduino.
     */
    public void run()
    {
        if(currentState != desiredState)
        {
            currentState = desiredState;
            setState();
        }
    }
}