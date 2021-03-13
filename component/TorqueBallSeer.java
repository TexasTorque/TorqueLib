package org.texastorque.torquelib.component;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * This class provides an interface for receiving communication from the ballseer.py vison code.
 * 
 * @author Jack
 * @apiNote This code was originally created during the 2021 season
 */
public class TorqueBallSeer {
    private NetworkTableInstance NT_instance;
    private NetworkTable NT_table;

    private NetworkTableEntry frame_width;
    private NetworkTableEntry frame_height;
    private NetworkTableEntry reset;
    private NetworkTableEntry target_location;

    public TorqueBallSeer() {
        NT_instance = NetworkTableInstance.getDefault();
        NT_table = NT_instance.getTable("BallSeer");
        
        frame_width = NT_table.getEntry("frame_width");
        frame_height = NT_table.getEntry("frame_height");
        reset = NT_table.getEntry("reset");
        target_location = NT_table.getEntry("target_location");
    }

    /**
     * @return the target_location
     */
    public double[] getTarget_location() {
        return target_location.getDoubleArray(new double[]{0,0});
    }

    /**
     * @return the frame_height
     */
    public double getFrame_height() {
        return frame_height.getDouble(0.0);
    }

    /**
     * @return the frame_width
     */
    public double getFrame_width() {
        return frame_width.getDouble(0.0);
    }

    /**
     * Sends a reset signal to BallSeer to reset current found ball
     */
    public void reset() {
        reset.setBoolean(true);
    }
}