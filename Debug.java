/**
 * Copyright 2023 Texas Torque.
 *
 * This file is part of Torque-2023, which is not licensed for distribution. For more details, see
 * ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib;

import java.util.HashMap;
import java.util.Map;

import org.texastorque.Subsystems;
import org.texastorque.torquelib.util.TorqueUtil;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

public final class Debug implements Subsystems {
    public static final boolean DO_LOGGING = true;

    private static final Map<String, Double> numbers = new HashMap<>();
    private static final Map<String, String> strings = new HashMap<>();
    private static final Map<String, Boolean> bools = new HashMap<>();

    public static void initDashboard() {
        Shuffleboard.update();

        final ShuffleboardTab dashboard = Shuffleboard.getTab("COMPETITION");
    }
        

    public static void log(final String key, final double number) {
        if (DO_LOGGING) {
            if (!numbers.containsKey(key)) {
                getTab().addNumber(key, () -> numbers.get(key));
            }
            numbers.put(key, number);
        }
    }

    public static void log(final String key, final String string) {
        if (DO_LOGGING) {
            if (!strings.containsKey(key)) {
                getTab().addString(key, () -> strings.get(key));
            }
            strings.put(key, string);
        }
    }

    public static void log(final String key, final boolean bool) {
        if (DO_LOGGING) {
            if (!bools.containsKey(key)) {
                getTab().addBoolean(key, () -> bools.get(key));
            }
            bools.put(key, bool);
        }
    }

    public static void field(final String key, final Field2d field) {
        if (DO_LOGGING) {
            getTab().add(key, field);
        }
    }

    private static ShuffleboardTab getTab() {
        final String className = TorqueUtil.getStackTraceElement(4).getClassName();
        String tabName = className.substring(className.lastIndexOf('.') + 1);
        if (tabName.contains("$")) {
            tabName = tabName.substring(0, tabName.lastIndexOf('$'));
        }
        return Shuffleboard.getTab(tabName);
    }
}
