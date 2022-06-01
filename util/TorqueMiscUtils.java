package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;

/**
 * Miscellaneous and uncatagorized static functions used throughout the codebase.
 * 
 * @apiNote The fancy print functions are not done yet.
 * 
 * @author Justus Languell
 */
public final class TorqueMiscUtils {
    private TorqueMiscUtils() {}

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
     * Defines either enclosing or parent.
     */
    public enum ElevationLevel {
        ENCLOSING, PARENT;
    }

    /*
    Parent calls enclosing like so.

    void parent() {
        enclosing();
    }

    Enclosing is what makes the call to a function that
    uses ElevationLevel.

    void enclosing() {
        getStackTraceElement(...);
    }
    */

    /**
     * Get either enclosing or parent stack trace element.
     * 
     * @param level Enclosing or Parent, from {@link ElevationLevel}.
     * 
     * @return The stack trace element at the desired elevation level.
     */
    public static final StackTraceElement getStackTraceElement(final ElevationLevel level) {
        return getStackTraceElement(level.ordinal());
    }

    /**
     * Get a stack trace element.
     * 
     * @param level The stack trace level. 
     * 
     * @return The stack trace element at the desired elevation level.
     */
    public static final StackTraceElement getStackTraceElement(final int level) {
        return Thread.currentThread().getStackTrace()[level];
    }

    /** 
     * Describes a print color and style.
     * 
     * @author Justus Languell.
     */
    public enum PrintColor {
        RESET(0),
        BLACK(30), RED(31), GREEN(32), YELLOW(33), 
        BLUE(34), MAGENTA(35), CYAN(36), WHITE(37);

        private final int index;
        private boolean isBold;

        private PrintColor(final int index) {
            this.index = index;
            this.isBold = false;
        }

        public final void makeBold() {
            this.isBold = true;
        }

        public final PrintColor bolded() {
            makeBold();
            return this;
        }

        public final String getCode() {
            final String code = String.format("\u001b[%d%sm", this.index, this.index != 0 && this.isBold ? ";1" : "");
            this.isBold = false;
            return code;
        }
    }

    /**
     * Enhanced print function that utalizes a single print formatting style,
     * a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param message The message to print.
     */
    public static final void print(final PrintColor color, final String message) {
        printfximpl(color, "%s", message);
    }

    /**
     * Enhanced print function that utalizes a single print formatting style
     * with a newline, a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param message The message to print.
     */
    public static final void println(final PrintColor color, final String message) {
        printfximpl(color, "%s\n", message);
    }

    /**
     * Enhanced print function that utalizes an appendable print formatting style,
     * a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param arguments The things to print.
     */
    public static final void print(final PrintColor color, final Object... arguments) {
        final StringBuilder builder = new StringBuilder();
        for (final Object argument : arguments)
            builder.append(argument);
        printfximpl(color, "%s", builder.toString());
    }

    /**
     * Enhanced print function that utalizes an appendable print formatting style
     * with a newline, a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param arguments The things to print.
     */
    public static final void println(final PrintColor color, final Object... arguments) {
        final StringBuilder builder = new StringBuilder();
        for (final Object argument : arguments)
            builder.append(argument);
        printfximpl(color, "%s\n", builder.toString());
    }

    /**
     * Enhanced print function that utalizes a printf formatting style,
     * a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param format Format string.
     * @param args Arguments to format.
     */
    public static final void printf(final PrintColor color, final String format, final Object... arguments) {
        printfximpl(color, format, arguments);
    }

     /**
     * Enhanced print function that utalizes a printf formatting style
     * with a newline, a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param format Format string.
     * @param args Arguments to format.
     */
    public static final void printfln(final PrintColor color, final String format, final Object... arguments) {
        printfximpl(color, format + '\n', arguments);
    }

    /**
     * Lowest level of abstraction for the enhanced print functions.
     * 
     * @param color PrintColor configuration.
     * @param format Format string.
     * @param args Arguments to format.
     */
    private static final void printfximpl(final PrintColor color, final String format, final Object... arguments) {
        final StackTraceElement parent = getStackTraceElement(4);
        System.out.printf(String.format("%s.%s -> ", parent.getClassName(), parent.getMethodName())
                + color.getCode() + format + PrintColor.RESET.getCode(), arguments);
    }

    /**
     * Kills the program and writes an error message with a printf style.
     */
    public static final void errorf(final String format, final Object... args) {
        System.err.printf(format, args);
        System.exit(1);
    }

    /**
     * Automatically kills the program and displays that this method 
     * is unimplemented to the console.
     */
    public static final void notImplemented() {
        final StackTraceElement parent = getStackTraceElement(ElevationLevel.PARENT);
        errorf("ERROR: %s.%s is not implemented.\n", parent.getClassName(), parent.getMethodName());
    }

    /**
     * Main function to run tests on this class.
     */
    public static final void main(final String[] arguments) {
        print(PrintColor.GREEN, "Hello, World!\n");
        println(PrintColor.GREEN, "Hello, World!");
        print(PrintColor.RED, "This", " is ", 5, " and ", 7, "\n");
        println(PrintColor.RED, "This", " is ", 5, " and ", 7);
        printf(PrintColor.BLUE, "This is %d and %d\n", 6, 8); 
        printfln(PrintColor.BLUE, "This is %d and %d", 6, 8); 
    }

}
