package org.texastorque.torquelib.component;

import java.util.ArrayList;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.texastorque.util.KPID;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/*
 
 This class is
  _  _  ___ _____   ___   ___  _  _ ___ 
 | \| |/ _ \_   _| |   \ / _ \| \| | __|
 | .` | (_) || |   | |) | (_) | .` | _| 
 |_|\_|\___/ |_|   |___/ \___/|_|\_|___|

 Please do not try an used it

 */

/** 
 * A class for controlling a Falcon 500.
 * The Falcon 500 uses a TalonFX Motor controller.
 * I'm not extending TorqueMotor for a reason - it is over
 * abstraction that adds nothing. The Falcon 500 and TalonFX
 * actually also add a wealth of features that TorqueMotor
 * is not equipped to handle without heavy modification for
 * standardization.
 * 
 * Resources:
 * https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/blob/master/Java%20Talon%20FX%20(Falcon%20500)/IntegratedSensor/src/main/java/frc/robot/Robot.java
 * http://www.ctr-electronics.com/downloads/pdf/Falcon%20500%20User%20Guide.pdf
 * 
 * @deprecated NOT COMPLETE
 * 
 * @author TexasTorque
 */
@Deprecated // NOT COMPLETE
public class TorqueFalcon { 

    /**
     * TODO: Add TalonFXInvertType direction setting mode. 
     * 
     */

    private final double kUnitsPerRev = 2048.; // CPR of the TalonFX

    private TalonFX falcon;
    private TalonFXConfiguration config;
    private ArrayList<TalonFX> falconFollowers = new ArrayList<>();
    private boolean invert = false;

    private NeutralMode neutralMode = NeutralMode.EEPROMSetting;

    // Constructors for main motor

    public TorqueFalcon(int port) {
        falcon = new TalonFX(port);
        // configFactoryDefault is the old API
        //falcon.configFactoryDefault();

        // New API to use integrated encoder
        config = new TalonFXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;
        falcon.configAllSettings(config);
        falcon.setNeutralMode(neutralMode);
    }

    public TorqueFalcon(int port, NeutralMode neutralMode) {
        falcon = new TalonFX(port); 

        config = new TalonFXConfiguration();
        config.primaryPID.selectedFeedbackSensor = FeedbackDevice.IntegratedSensor;
        falcon.configAllSettings(config);

        this.neutralMode = neutralMode;
        falcon.setNeutralMode(neutralMode);
    }

    // Handles addition of followers

    public void addFollower(int port) {
        TalonFX localFalcon = new TalonFX(port);
        localFalcon.setNeutralMode(neutralMode);
        falconFollowers.add(localFalcon);
    }

    public void addFollower(int port, boolean inverted) {
        TalonFX localFalcon = new TalonFX(port);
        localFalcon.setInverted(inverted);
        localFalcon.setNeutralMode(neutralMode);
        falconFollowers.add(localFalcon);
    }

    // Handles inversions
    
    public void setInverted(boolean invert) {
        falcon.setInverted(invert);
    }

    public void setInvertedAll(boolean invert) {
        falcon.setInverted(invert);
        for (TalonFX follower : falconFollowers) {
            follower.setInverted(invert);
        }
    }

    // Handles neutral mode in brake etc.

    private void updateNeutralMode() {
        falcon.setNeutralMode(neutralMode);
        for (TalonFX follower : falconFollowers) {
            follower.setNeutralMode(neutralMode);
        }
    }

    public void setNeutralMode(NeutralMode mode) {
        neutralMode = mode;
        updateNeutralMode();
    }

    public void resetNeutralMode() {
        neutralMode = NeutralMode.EEPROMSetting;
        updateNeutralMode();
    }

    public NeutralMode getNeutralMode() {
        return neutralMode;  
    }

    






}