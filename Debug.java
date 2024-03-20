/**
 * Copyright 2023 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib;

import java.util.HashMap;
import java.util.Map;

import org.texastorque.Subsystems;
import org.texastorque.torquelib.util.TorqueMath;
import org.texastorque.torquelib.util.TorqueUtil;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

public final class Debug implements Subsystems {

    public static record Point(int x, int y) {};

    public static class Grid {
        private int width, currentPt = 0;

        public static Grid eslastic() {
            return new Grid(12);
        }

        public Grid(int width) {
            this.width = width;
        }

        /** 
         * This doesnt have to me most time efficient algorithm 
         * b/c we only compute this once per entry.
         * 
         * TODO: make this an actual algorithm.
         */
        public Point findSpot(final int w) {
            final var pt = new Point(currentPt % width, currentPt / width);
            currentPt += w;
            return pt;
        }
    }

    private static final Map<Integer, Map<String, Double>> numbers = new HashMap<>();
    private static final Map<Integer, Map<String, String>> strings = new HashMap<>();
    private static final Map<Integer, Map<String, Boolean>> bools = new HashMap<>();
    private static final Map<Integer, Grid> grids = new HashMap<>();

    public static void initDashboard() {
        Shuffleboard.update();
    }
    
    public static void log(final String key, final double number) {
        log(getTab(), key, number, 2, 1);
    }
    public static void log(final String key, final double number, final int width) {
        log(getTab(), key, number, width, 1);
    }

    private static void log(final ShuffleboardTab tab, final String key, final double number, final int width, final int height) {
        final int tabCode = tab.hashCode();
        if (!grids.containsKey(tabCode)) {
            grids.put(tabCode, Grid.eslastic()); 
        }
        
        if (!numbers.containsKey(tabCode)) {
            numbers.put(tabCode, new HashMap<String, Double>());
        }

        if (!numbers.get(tabCode).containsKey(key)) {
            final var pt = grids.get(tabCode).findSpot(width);
            tab.addNumber(key, () -> numbers.get(tabCode).get(key)).withSize(width, height).withPosition(pt.x, pt.y);
        }
        numbers.get(tabCode).put(key, TorqueMath.round(number, 4));
    }

    public static void log(final String key, final String string) {
        log(getTab(), key, string, 2, 1);
    }
    public static void log(final String key, final String string, final int width) {
        log(getTab(), key, string, width, 1);
    }

    private static void log(final ShuffleboardTab tab, final String key, final String string, final int width, final int height) {
        final int tabCode = tab.hashCode();
        if (!grids.containsKey(tabCode)) {
            grids.put(tabCode, Grid.eslastic()); 
        }
        
        if (!strings.containsKey(tabCode)) {
            strings.put(tabCode, new HashMap<String, String>());
        }

        if (!strings.get(tabCode).containsKey(key)) {
            final var pt = grids.get(tabCode).findSpot(width);
            tab.addString(key, () -> strings.get(tabCode).get(key)).withSize(width, height).withPosition(pt.x, pt.y);
        }
        strings.get(tabCode).put(key, string);
    }

    public static void log(final String key, final boolean bool) {
        log(getTab(), key, bool, 2, 1);
    }
    public static void log(final String key, final boolean bool, final int width) {
        log(getTab(), key, bool, width, 1);
    }
    private static void log(final ShuffleboardTab tab, final String key, final boolean bool, final int width, final int height) {
        final int tabCode = tab.hashCode();
        if (!grids.containsKey(tabCode)) {
            grids.put(tabCode, Grid.eslastic()); 
        }
        
        if (!bools.containsKey(tabCode)) {
            bools.put(tabCode, new HashMap<String, Boolean>());
        }

        if (!bools.get(tabCode).containsKey(key)) {
            final var pt = grids.get(tabCode).findSpot(width);
            tab.addBoolean(key, () -> bools.get(tabCode).get(key)).withSize(width, height).withPosition(pt.x, pt.y);
        }
        bools.get(tabCode).put(key, bool);
    }

    public static void field(final String key, final Field2d field) {
        getTab().add(key, field);
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
