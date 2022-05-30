package org.texastorque.torquelib.util;

/**
 * Uncatagorized static functions used throughout the codebase.
 * 
 * @author Justus Languell
 */
public final class TorqueUtils {

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
        return Thread.currentThread().getStackTrace()[level.ordinal() + 2];
    }

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
            return String.format("\u001b[%d%sm", this.index, this.index != 0 && this.isBold ? ";1" : "");

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
        printf(color, "%s", message);
    }

    /**
     * Enhanced print function that utalizes a printf formatting style,
     * a color, and notes the function it was called from.
     * 
     * @param color PrintColor configuration.
     * @param format Format string.
     * @param args Arguments to format.
     */
    public static final void printf(final PrintColor color, final String format, final Object arguments) {
        final StackTraceElement parent = getStackTraceElement(ElevationLevel.PARENT);
        System.out.printf(String.format("%s.%s says:", parent.getClassName(), parent.getMethodName()),
                color.getCode() + format + PrintColor.RESET.getCode(), arguments);
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
        printf(PrintColor.GREEN, "Hello, World!\n");
    }

}
