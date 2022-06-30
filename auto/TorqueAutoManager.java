package org.texastorque.torquelib.auto;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.HashMap;
import org.texastorque.torquelib.auto.sequences.TorqueEmpty;

/**
 * AutoManager base class. Handles backend methods
 * and containers.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Justus Languell
 * @author Jack Pittenger
 */
public abstract class TorqueAutoManager {
    private final HashMap<String, TorqueSequence> autoSequences;
    private final SendableChooser<String> autoSelector = new SendableChooser<String>();

    private TorqueSequence currentSequence;
    private boolean sequenceEnded;

    private final String autoSelectorKey = "Auto List";

    public TorqueAutoManager() {
        autoSequences = new HashMap<String, TorqueSequence>();

        addSequence("Empty", new TorqueEmpty("Empty")); // default

        init();
        displayChoices();
    }

    /**
     * This is where we add sequenes
     */
    protected abstract void init();

    protected final void addSequence(String name, TorqueSequence seq) {
        autoSequences.put(name, seq);

        if (autoSequences.size() == 0) {
            autoSelector.setDefaultOption(name, name);
        } else {
            autoSelector.addOption(name, name);
        }
    }

    public final void runCurrentSequence() {
        if (currentSequence != null) {
            currentSequence.run();
            sequenceEnded = currentSequence.hasEnded(); // manage state of sequence
        } else {
            DriverStation.reportError("No auto selected!", false);
        }
    }

    public final void chooseCurrentSequence() {
        String autoChoice = NetworkTableInstance.getDefault()
                                    .getTable("SmartDashboard")
                                    .getSubTable(autoSelectorKey)
                                    .getEntry("selected")
                                    .getString("N/A");

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
    public final void setCurrentSequence(TorqueSequence seq) {
        currentSequence = seq;
        resetCurrentSequence();
    }

    /**
     * Send sequence list to SmartDashboard
     */
    public final void displayChoices() { SmartDashboard.putData(autoSelectorKey, autoSelector); }

    public final void resetCurrentSequence() {
        if (currentSequence != null) currentSequence.reset();
    }

    /**
     * Return the state variable that shows whether the sequence is ended or not
     */
    public final boolean getSequenceEnded() { return sequenceEnded; }
}