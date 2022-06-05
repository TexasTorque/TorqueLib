package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;

/**
 * Miscellaneous and uncatagorized static functions used throughout the codebase.
 *
 * @author Justus Languell
 */
public final class TorqueMiscUtils {
    /**
     * Private constructor to prevent outside instantiation.
     * Can be instantiated by the main function to run tests.
     */
    private TorqueMiscUtils() {
        test();
    }

    /**
     * Main function to run tests on this class.
     */
    public static final void main(final String[] arguments) {
        new TorqueMiscUtils();
    }

    /**
     * Test functions in the class from non-static context.
     */
    private final void test() {
        System.out.printf("> %s\n", getStackTraceElement(30));
        warnf("This is a warning\n");
        errorf("This is an error\n");
        notImplemented();
    }

    public static final String osName = System.getProperty("os.name");
    public static final boolean onRobot = osName.equals("ROBORIO"); // TODO: This is a placeholder

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
}
