package org.texastorque.torquelib.util;

import java.util.Vector;

public class TorqueUtil {

    public static double convertToRMP(double unitsPerSecond, double unitsPerRevolution) {
        return (unitsPerSecond * 60) / unitsPerRevolution;
    }

    public static double applyDeadband(double value, double deadband) {
        if (Math.abs(value) <= deadband) {
            return 0.0;
        } else {
            return value;
        }
    }

    public static double sqrtHoldSign(double val) {
        int sign = (val > 0) ? 1 : -1;
        val = Math.sqrt(Math.abs(val)) * sign;
        return val;
    }

    /*
     *  Split is not provided as a JavaME String function.
     */
    public static String[] split(String input, String delimiter) {
        Vector node = new Vector();
        int index = input.indexOf(delimiter);

        while (index >= 0) {
            node.addElement(input.substring(0, index));
            input = input.substring(index + delimiter.length());
            index = input.indexOf(delimiter);
        }

        node.addElement(input);

        String[] retString = new String[node.size()];
        for (int i = 0; i < node.size(); ++i) {
            retString[i] = (String) node.elementAt(i);
        }

        return retString;
    }
}
