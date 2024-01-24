package org.texastorque.torquelib.control;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Generic lookup table. Use by giving a type and constructing with
 * interpolate and equals functions. Then add your options. 
 * 
 */
public class TorqueLookUpTable<T> {

    @FunctionalInterface
    public static interface TriFunction<A,B,C,R> {
        R apply(A a, B b, C c);
        default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c) -> after.apply(apply(a, b, c));
        }
    }

    public TreeMap<Double, T> table;

    private BiFunction<T, T, Boolean> equals;
    private TriFunction<T, T, Double, T> interpolate;

    public TorqueLookUpTable(BiFunction<T, T, Boolean> equals, TriFunction<T, T, Double, T> interpolate) {
        this.table = new TreeMap<Double, T>();

        this.equals = equals;
        this.interpolate = interpolate;
    }

    public void add(final double key, final T value) {
        table.put(key, value);
    }

    public T get(final double key) {
        Entry<Double, T> ceil = table.ceilingEntry(key);
        Entry<Double, T> floor = table.floorEntry(key);

        if (ceil == null) return floor.getValue();
        if (floor == null) return ceil.getValue();

        if (equals.apply(ceil.getValue(), floor.getValue()))
            return ceil.getValue();

        return interpolate.apply(floor.getValue(), ceil.getValue(),  (key - floor.getKey()) / (ceil.getKey() - floor.getKey()) );
    }

}
