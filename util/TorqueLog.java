package org.texastorque.torquelib.util;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;

public final class TorqueLog {
    
    public final int COLUMNS = 10;

    public final String title;
    private final ShuffleboardTab tab;
    // We are just adding keys we have used to a hashmap so we can check
    // if we have already used them in O(1) time instead of O(n).
    // The value and type of the entry doesnt matter so we will just
    // be adding "true" values.
    private final HashMap<String, NetworkTableEntry> keys; 

    private int x = 0, y = 0;

    public TorqueLog(final String title) {
        this.title = title;
        tab = Shuffleboard.getTab(title);
        keys = new HashMap<String, NetworkTableEntry>();
    }



    // public void log(final String key, final Object value, final int width, final int height, final WidgetType type) {
        
    //     if (!keys.containsKey(key)) {
    //         final NetworkTableEntry entry = tab.add(key, value).withWidget(type).withSize(0, 0).withPosition(0, 0).getEntry();
    //         x += 
    //         keys.put(key, entry);
    //         return;
    //     }


    
    
    
    // }




}
