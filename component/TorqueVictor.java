package org.texastorque.torquelib.component;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class TorqueVictor extends VictorSPX {
    public TorqueVictor(int port){
        super(port);
    }
    
    public void set(double val) {
        set(ControlMode.PercentOutput, val);
    }
}