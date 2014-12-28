package org.texastorque.torquelib.arduino;

import edu.wpi.first.wpilibj.SPI;

public class MultiWiiGyro {

    private SPI MultiWii;
    
    byte desiredByte;
    private final byte bigByte = 0;
    private final byte smallByte = 1;
    
    private byte[] receiveData;
    private double angle;
    private double newAngle;

    public MultiWiiGyro() {
        MultiWii = new SPI(SPI.Port.kOnboardCS0);
        MultiWii.setMSBFirst();
        MultiWii.setClockActiveHigh();
        MultiWii.setClockRate(2000000);
        MultiWii.setChipSelectActiveLow();
        MultiWii.setSampleDataOnRising();
        
        receiveData = new byte[1];
    }
    
    public double getAngle() {
        return angle;
    }
    
    public void run() {
        byte[] temp = new byte[1];
        temp[0] = desiredByte;
        MultiWii.transaction(temp, receiveData, 1);
        
        if (desiredByte == 1) {
            newAngle = receiveData[0];
        } else {
            newAngle += (receiveData[0] << 8);
            angle = newAngle;
        }
    }
}
