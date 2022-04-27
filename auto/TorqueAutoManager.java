package org.texastorque.torquelib.auto;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * AutoManager base class. Handles backend methods
 * and containers.
 * 
 * Part of the Texas Torque Autonomous Framework.
 * 
 * @author Jack, Justus
 */
public abstract class TorqueAutoManager {
    private HashMap<String, TorqueSequence> autoSequences;
    private SendableChooser<String> autoSelector = new SendableChooser<String>();

    private TorqueSequence currentSequence;
    private boolean sequenceEnded;

    private final String autoSelectorKey = "AutoList";

    public TorqueAutoManager() {
        autoSequences = new HashMap<String, TorqueSequence>();

        addSequence("Empty", new TorqueEmpty("Empty")); // default

        init();
        displayChoices();
    }

    protected abstract void init(); // this is where we add sequences

    protected void addSequence(String name, TorqueSequence seq) {
        autoSequences.put(name, seq);

        if (autoSequences.size() == 0) {
            autoSelector.setDefaultOption(name, name);
        } else {
            autoSelector.addOption(name, name);
        }
    }

    public void runCurrentSequence() {
        if (currentSequence != null) {
            currentSequence.run();
            sequenceEnded = currentSequence.hasEnded(); // manage state of sequence
        } else {
            DriverStation.reportError("No auto selected!", false);
        }
    }

    public void chooseCurrentSequence() {
        String autoChoice = NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(autoSelectorKey)
                .getEntry("selected").getString("N/A");

        if (autoSequences.containsKey(autoChoice)) {
            System.out.println("Switching to auto: " + autoChoice);
            currentSequence = autoSequences.get(autoChoice);
        }

        resetCurrentSequence();
        sequenceEnded = false;
    }

    /**
     * Set sequence with sequence object
     */
    public void setCurrentSequence(TorqueSequence seq) {
        currentSequence = seq;
        resetCurrentSequence();
    }

    /**
     * Send sequence list to SmartDashboard
     */
    public void displayChoices() {
        SmartDashboard.putData(autoSelectorKey, autoSelector);
    }

    public void resetCurrentSequence() {
        if (currentSequence != null)
            currentSequence.reset();
    }

    /**
     * Return the state variable that shows whether the sequence is ended or not
     */
    public boolean getSequenceEnded() {
        return sequenceEnded;
    }
}