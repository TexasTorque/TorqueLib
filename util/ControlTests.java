package org.texastorque.torquelib.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ControlTests {
    public static final void main(final String[] arguments) {
        new ControlTests();
    }

    private ControlTests() {
        init();
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> update(), 0, 500, TimeUnit.MILLISECONDS);


    }

    private void init() {
        System.out.println("init()");
    }

    final TorqueSubsequent s = new TorqueSubsequent();

    private void update() {

        // if (s.calculate()) {
        //     System.out.println("true");
        // } else {
        //     System.out.println("false");
        // }

        s.execute(() -> {
            System.out.println("true");
        }, () -> {
            System.out.println("false");
        });
    }

}
