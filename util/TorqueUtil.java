/**
 * Copyright 2011-2022 Texas Torque.
 *
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@gtsbr.org>.
 */
package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Miscellaneous and uncatagorized static functions used throughout the codebase.
 *
 * @author Justus Languell
 */
public final class TorqueUtil {
    private TorqueUtil() { TorqueUtil.staticConstructor(); }

    public static final String osName = System.getProperty("os.name");
    // public static final boolean onRobot = RobotBase.isReal();
    //public static final boolean onRobot = !osName.equals("Mac OS X");
    public static final boolean onRobot = osName.equals("Linux");
    static {
        if (onRobot) SmartDashboard.putString("OSNAME", osName);
    }

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
        errorf("ERROR: %s is a static class and cannot be instantiated.", getStackTraceElement(3).getClassName());
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
        return new UnsupportedOperationException(String.format(
                "ERROR: %s is a static class and cannot be instantiated.", getStackTraceElement(3).getClassName()));
    }

    /**
     * Enumerated For (enumeratedFor) is a class that *trys* to
     * implement similar functionality to Python:
     *
     *  for i, element in enumerate(iterable):
     *      doSomething(i, element)
     *
     * in the form:
     *
     *  <T>enumeratedFor(iterable, (i, element) -> {
     *      doSomething(i, element);
     *  });
     *
     * So not that much more verbose, but you do have to specify type!
     *
     * @param <T> Type of the iterable.
     * @param iterable The iterable to iterate over.
     * @param f The functional interface to call.
     *
     * @author Justus Languell
     */
    public static final <T> void enumeratedFor(final Iterable<T> iterable, final BiConsumer<Integer, T> f) {
        int i = 0;
        for (final T element : iterable) f.accept(i++, element);
    }

    /**
     * A wrapper for enumeratedFor that supports static arrays.
     *
     * @param <T> Type of the iterable.
     * @param array The array to iterate over.
     * @param f The functional interface to call.
     */
    public static final <T> void enumeratedFor(final T[] array, final BiConsumer<Integer, T> f) {
        enumeratedFor(Arrays.asList(array), f);
    }

    public static final class Pair<T, U> {
        public final T first;
        public final U second;

        public Pair(final T first, final U second) {
            this.first = first;
            this.second = second;
        }
    }

    public static final class Triple<T, U, V> {
        public final T first;
        public final U second;
        public final V third;

        public Triple(final T first, final U second, final V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public final static class TimeResult<T> {
        public final T result;
        public final double time;

        public TimeResult(final T result, final double time) {
            this.result = result;
            this.time = time;
        }
    }

    public static final <T> TimeResult<T> time(final Supplier<T> f) throws Exception {
        final double start = time();
        final T result = f.get();
        return new TimeResult<>(result, (time() - start) / 1000.);
    }

    /**
     * Apply or don't modify a value based on a condition using a transforming function.
     *
     * @param <T> Type or parameter and return.
     * @param use To modify or not to modify.
     * @param value The input value.
     * @param function The function that modifies the input value.
     * @return The modified or unmodified value.
     */
    public static final <T> T conditionalApply(final boolean use, final T value, final Function<T, T> function) {
        return use ? function.apply(value) : value;
    }
}
