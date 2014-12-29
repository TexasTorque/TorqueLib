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
        
        desiredByte = 1;
        receiveData = new byte[1];
    }
    
    public double getAngle() {
        return angle;
    }
    
    public void run() {
        byte[] temp = new byte[1];
        
        temp[0] = 0;
        MultiWii.transaction(temp, receiveData, 1);
        int low = receiveData[0];
        
	if (low>>7 == -1) {
		low += 256;
	}
		
        temp[0] = 1;
        MultiWii.transaction(temp, receiveData, 1);
        int high = receiveData[0];
        
        angle = (high << 8) + low;
    }
}
