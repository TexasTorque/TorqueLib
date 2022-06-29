package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Miscellaneous and uncatagorized static functions used throughout the codebase.
 *
 * @author Justus Languell
 */
public final class TorqueMiscUtil {
    private TorqueMiscUtil() { TorqueMiscUtil.staticConstructor(); }

    public static final String osName = System.getProperty("os.name");
    //static { SmartDashboard.putString("OSNAME", osName); }
    //public static final boolean onRobot = RobotBase.isReal();
    public static final boolean onRobot = !osName.equals("Mac OS X");

    /**
     * A time method that can be used on the robot and
     * on a computer. Should really only be used for relative use.
     *
     * @returns The current time in seconds.
     */
    public static final double time() {
        return onRobot ? Timer.getFPGATimestamp() : System.currentTimeMillis() / 1000.;
    }

    /**
     * Get a stack trace element.
     *
     * @param level The stack trace level.
     *
     * @return The stack trace element at the desired elevation level.
     */
    public static final StackTraceElement getStackTraceElement(final int level) {
        try {
            return Thread.currentThread().getStackTrace()[level];
        } catch (ArrayIndexOutOfBoundsException e) {
            warnf("Trying to access a stack trace element out of bounds!\n");
            return null;
        }
    }

    /**
     * Writes an error message with a printf style in yellow but doesn't kill the program.
     */
    public static final void warnf(final String format, final Object... args) {
        System.err.printf(String.format("\u001b[%dm%s\u001b[%dm", 33, format, 0), args);
    }

    /**
     * Kills the program and writes an error message with a printf style in red.
     */
    public static final void errorf(final String format, final Object... args) {
        System.err.printf(String.format("\u001b[%dm%s\u001b[%dm", 31, format, 0), args);
        System.exit(1);
    }

    /**
     * Automatically kills the program and displays that this method
     * is unimplemented to the console.
     */
    public static final void notImplemented() {
        final StackTraceElement parent = getStackTraceElement(3);
        errorf("ERROR: %s.%s is not implemented.\n", parent.getClassName(), parent.getMethodName());
    }

      /**
     * Automatically kills the program and displays that this method
     * is unimplemented to the console.
     */
    public static final void outOfDate() {
        final StackTraceElement parent = getStackTraceElement(3);
        errorf("ERROR: %s.%s is not out of date.\n", parent.getClassName(), parent.getMethodName());
    }

    /**
     * Automatically kills the program and displays that we are trying to
     * instantiate the constructor of a static class.
     */
    public static final void staticConstructor() {
        errorf("ERROR: %s is a static class and cannot be instantiated.",
                getStackTraceElement(3).getClassName()); 
    }

    /**
     * Returns a throwable exception that notifies the developer that they are
     * attempting to instantiate a static class.
     * 
     * Call this in static constructors like so:
     * throw staticConstructor();
     * 
     * @return The throwable exception.
     */
    public static final UnsupportedOperationException staticConstructorError() {
        return new UnsupportedOperationException(String.format("ERROR: %s is a static class and cannot be instantiated.",
                getStackTraceElement(3).getClassName()));
    }
}
