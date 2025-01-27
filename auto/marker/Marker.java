package org.texastorque.torquelib.auto.marker;

public class Marker {

    private final Runnable command;
    private final double relativePosition;
    private boolean hasRan;

    public Marker(final Runnable command, final double relativePosition) {
        this.command = command;
        this.relativePosition = relativePosition;
    }

    public void run() {
        command.run();

        hasRan = true;
    }

    public boolean hasRan() {
        return hasRan;
    }

    public double getRelativePosition() {
        return relativePosition;
    }
}