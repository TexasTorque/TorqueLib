package org.texastorque.torquelib.component;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.texastorque.util.KPID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TorqueTalon extends TorqueMotor {
    private TalonSRX talon;
    private ArrayList<TalonSRX> talonFollowers = new ArrayList<TalonSRX>();
    private boolean invert = false;

    // ===================== constructor stuff =================
    public TorqueTalon(int port){
        talon = new TalonSRX(port);
        this.port = port;
    } // torque talon 

    @Override
    public void addFollower(int port) {
        talonFollowers.add(new TalonSRX(port));
    } // add follower 

    public void addFollower(int port, boolean invert) {
        talonFollowers.add(new TalonSRX(port));
        
    } // add follower 

    // ====================== set methods ==========================
    @Override 
    public void set(double output){
        talon.set(ControlMode.PercentOutput, output);
        for(TalonSRX talonSRX : talonFollowers){
            talonSRX.set(ControlMode.Follower, port);
            talonSRX.setInverted(invert);
            SmartDashboard.putNumber("FollowerVelocity", output);
        } // takes care of followers 
    } // generic set method 

    public void set(double output, ControlMode modeTalon){
        talon.set(modeTalon, output);
        for(TalonSRX talonSRX : talonFollowers){
            talonSRX.set(ControlMode.Follower, port);
        } // takes care of followers 
    } // set with ControlMode for talon 

    // ====================== pid stuff ==========================

    @Override
    public void configurePID(KPID kPID) {
        talon.config_kP(0, kPID.p());
        talon.config_kI(0, kPID.i());
        talon.config_kD(0, kPID.d());
        talon.config_kF(0, kPID.f());
        talon.configPeakOutputForward(kPID.max());
        talon.configPeakOutputReverse(kPID.min());
    } // configure PID 

    @Override
    public void updatePID(KPID kPID) {
        talon.config_kP(0, kPID.p());
        talon.config_kI(0, kPID.i());
        talon.config_kD(0, kPID.d());
        talon.config_kF(0, kPID.f());
    } // update PID 

    @Override
    public double getVelocity() {
        try{
            return talon.getSelectedSensorVelocity();
        } catch (Exception e){
            System.out.println(e);
            System.out.println("There is no encoder present, you need to put one in");
        }
        return 0;
    } // get position 

    @Override
    public double getPosition() {
        try{
            return talon.getSelectedSensorPosition();
        } catch (Exception e){
            System.out.println(e);
            System.out.println("There is no encoder present, you need to put one in");
        }
        return 0;
    } // get position

    public double getOutput(){
        return talon.getMotorOutputPercent();
    }

    public void invertFollower(){
		invert = !invert;
	} // invert follower - flips the direction of the follower from what it was previously, default direction is same as leader 
} // Torque Talon 