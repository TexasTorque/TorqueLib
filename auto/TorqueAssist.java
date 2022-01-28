package org.texastorque.torquelib.auto;

import org.texastorque.inputs.Input;
import org.texastorque.torquelib.base.TorqueInput;
import org.texastorque.torquelib.base.TorqueInputManager;
import org.texastorque.torquelib.util.GenericController;

/**
 * TexasTorque Assist Sequence Manager
 * 
 * Brings command based paradigm to the timed based 
 * project by wrapping autonomous sequences for use
 * in the input class.
 * 
 * Part of the Texas Torque Autonomous Framework.
 * 
 * @author Justus, Jack
 */
public class TorqueAssist {
    TorqueSequence sequence;
    TorqueInput[] inputs;
    boolean done;

    public TorqueAssist(TorqueSequence sequence) {
        this.sequence = sequence;
        done = false;
    }

    public TorqueAssist(TorqueSequence sequence, TorqueInput... inputs) {
        this.sequence = sequence;
        done = false;
        this.inputs = inputs;
    }

    public void requires(TorqueInput... inputs) {
        this.inputs = inputs;
    }

    public void run(boolean condition) {
        setRequired(condition);
        if (condition) {
            if (!done)
                sequence.run();
            done = sequence.hasEnded();
        } else {
            done = false;
            sequence.reset();
        }
    }

    private void setRequired(boolean condition) {
        for (TorqueInput input : inputs)
            if (condition)
                input.block();

    }
}