/**
 * Copyright 2011-2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.auto.sequences;

import org.texastorque.torquelib.auto.TorqueCommand;
import org.texastorque.torquelib.auto.TorqueSequence;

/**
 * An empty TorqueSequence for use as default by TorqueAutoManager.
 *
 * Part of the Texas Torque Autonomous Framework.
 *
 * @author Justus
 */
public final class TorqueRunCommand extends TorqueSequence {
    public TorqueRunCommand(final TorqueCommand command) {
        addBlock(command);
    }
}
