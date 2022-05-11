package org.texastorque.torquelib.auto;

import java.util.ArrayList;

/**
 * Basically a typedef to wrap ArrayList<TorqueCommand> with Block
 * 
 * Part of the Texas Torque Autonomous Framework.
 * 
 * @author Justus
 */
public class TorqueBlock extends ArrayList<TorqueCommand> {
    public TorqueBlock() {
        super();
    }

    public TorqueBlock(TorqueCommand... commands) {
        for (TorqueCommand command : commands) {
            add(command);
        }
    }

    public final void addCommand(TorqueCommand command) {
        add(command);
    }
}