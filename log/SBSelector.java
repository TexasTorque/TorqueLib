package org.texastorque.torquelib.log;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SBSelector<T> {
    private final HashMap<String, T> options;
    private final SendableChooser<String> selector;
    private final String name;

    public SBSelector(String name) {
        options = new HashMap<String, T>();
        selector = new SendableChooser<String>();
        this.name = name;
    }

    public void add(String name, T value) {
        if (options.size() == 0)
            selector.setDefaultOption(name, name);
        else 
            selector.addOption(name, name);

        options.put(name, value);
        update();
    }

    public void update() {
        SmartDashboard.putData(name, selector);
    }

    public T get() {
        return options.get(NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(name)
                .getEntry("selected").getString("N/A"));
    }
}
