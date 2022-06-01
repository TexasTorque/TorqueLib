package org.texastorque.torquelib.control;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.texastorque.torquelib.motors.TorqueFalcon;

public final class Test {
    public static final void main(final String[] arguments) {
        new Test();
    }

    private Test() {
        init();
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> update(), 0, 500, TimeUnit.MILLISECONDS);

        // final ScheduledExecutorService swapper = Executors.newScheduledThreadPool(1);
        // swapper.scheduleAtFixedRate(() -> {
        //     state = !state;
        // }, 0, 3000, TimeUnit.MILLISECONDS);
    }

    private boolean state = false;

    private final TorqueClick click = new TorqueClick();

    private final void init() {
        var t = new TorqueConcurrentTimer();
        System.out.println(System.getProperty("os.name"));
    }

    private final void update() {
        System.out.printf("State: %b, Cals: %b\n", state, click.calculate(state));

    }

}
