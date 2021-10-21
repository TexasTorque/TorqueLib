package org.texastorque.torquelib.torquelog;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * This class provides interfaces for semantically outputing messages to
 * console.
 */
public class ConsoleLog {

    public enum ConsoleLogType {
        DEBUG("?"), INFO("[] "), OK(":)"), WARNING("0-0️"), ERROR("XXX");

        private String flag;

        private ConsoleLogType(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }

    }

    private static String identifier = "[Robot]";

    private ConsoleLog() {
    }

    /**
     * Print a message with identifier and custom type
     * 
     * @param type    The type of message to print
     * @param message The message to print
     */
    public static void printMessage(ConsoleLogType type, String message) {
        switch (type) {
            case WARNING:
                DriverStation.reportWarning(identifier + " " + message, false);
                break;
            case ERROR:
                DriverStation.reportError(identifier + " " + message, false);
                break;
            default:
                System.out.println(String.format("%s %s %s", type.getFlag(), identifier, message));
                break;
        }
    }

    public static void setIdentifier(String identifier) {
        ConsoleLog.identifier = "[" + identifier + "]";
    }

    public static String getIdentifier() {
        return identifier;
    }

}
