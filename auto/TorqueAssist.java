package org.texastorque.torquelib.auto;

import org.texastorque.torquelib.base.TorqueInput;

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
    public static enum AssistMode {
        RESET_SEQ,
        RESET_BLOCK
    }

    private AssistMode mode;
    private TorqueSequence sequence;
    private TorqueInput[] inputs;
    boolean done;
    private boolean lastCondition;

    public TorqueAssist(TorqueSequence sequence) {
        this.sequence = sequence;
        this.done = false;
        this.mode = AssistMode.RESET_SEQ;
    }

    public TorqueAssist(TorqueSequence sequence, AssistMode mode) {
        this.sequence = sequence;
        this.done = false;
        this.mode = mode;
    }

    public TorqueAssist(TorqueSequence sequence, TorqueInput... inputs) {
        this.sequence = sequence;
        this.done = false;
        this.inputs = inputs;
        this.mode = AssistMode.RESET_SEQ;
    }

    public TorqueAssist(TorqueSequence sequence, AssistMode mode, TorqueInput... inputs) {
        this.sequence = sequence;
        this.done = false;
        this.inputs = inputs;
        this.mode = mode;
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
            if (lastCondition != condition) {
                if (mode == AssistMode.RESET_SEQ)
                    sequence.reset();
                else if (mode == AssistMode.RESET_BLOCK)
                    sequence.resetBlock();
            }
        }
        lastCondition = condition;
    }

    private void setRequired(boolean condition) {
        for (TorqueInput input : inputs)
            if (condition)
                input.block();

    }
}