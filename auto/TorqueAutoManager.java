/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto;

import java.util.HashMap;

import org.texastorque.torquelib.auto.sequences.TorqueEmpty;
import org.texastorque.torquelib.util.TorqueUtil;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    /**
     * A preconstructed blank automanager.
     */
    public static final class TorqueBlankAutoManager extends TorqueAutoManager {
        @Override
        protected void init() {}
    }
    private final HashMap<String, TorqueSequence> autoSequences;

    private final SendableChooser<String> autoSelector = new SendableChooser<String>();

    private TorqueSequence currentSequence;
    private boolean sequenceEnded;

    private final String autoSelectorKey = "Auto List";

    protected TorqueAutoManager() { this(true); }

    protected TorqueAutoManager(final boolean displayChoicesSmartDashboard) {
        autoSequences = new HashMap<String, TorqueSequence>();

        addSequence("Empty", new TorqueEmpty()); // default

        init();
        if (displayChoicesSmartDashboard) displayChoices();
    }

    public final SendableChooser<String> getAutoSelector() { return autoSelector; }

    public final void runCurrentSequence() {
        if (currentSequence != null) {
            currentSequence.run();
            sequenceEnded = currentSequence.hasEnded(); // manage state of sequence
        } else
            DriverStation.reportError("No auto selected!", false);
    }

    public final void chooseCurrentSequence() {
        // final String autoChoice = NetworkTableInstance.getDefault()
        //                             .getTable("SmartDashboard")
        //                             .getSubTable(autoSelectorKey)
        //                             .getEntry("selected")
        //                             .getString("N/A");

        final String autoChoice = autoSelector.getSelected();

        if (autoSequences.containsKey(autoChoice)) currentSequence = autoSequences.get(autoChoice);

        resetCurrentSequence();
        sequenceEnded = false;
    }

    /**
     * Set sequence with sequence object
     */
    public final void setCurrentSequence(final TorqueSequence seq) {
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

    /**
     * This is where we add sequenes
     */
    protected abstract void init();

    protected final void addSequence(final TorqueSequence seq) {
        addSequence(TorqueUtil.camelCaseToTitleCase(seq.getClass().getSimpleName()), seq);
    }

    protected final void addSequence(final String name, final TorqueSequence seq) {
        autoSequences.put(name, seq);

        if (autoSequences.size() == 0)
            autoSelector.setDefaultOption(name, name);
        else
            autoSelector.addOption(name, name);
    }
}