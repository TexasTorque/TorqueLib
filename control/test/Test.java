package org.texastorque.torquelib.control.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.texastorque.torquelib.control.TorqueTimeout;
import org.texastorque.torquelib.motors.TorqueFalcon;

public final class Test {
    public static final String TEST_FILE_PATH = "/Users/justuslanguell/TexasTorque/TexasTorque2022/src/main/java/org/texastorque/torquelib/control/test.txt";

    public static final void main(final String[] arguments) {
        new Test();
    }

    public static final boolean action() { 
        try {
            final Scanner s = new Scanner(new File(TEST_FILE_PATH));
            if (!s.hasNext()) return false;
            return s.next().equals("true");
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    private final Scanner scanner = new Scanner(System.in);

    private Test() {
        init();

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> update(), 0, 500, TimeUnit.MILLISECONDS);

        // final ScheduledExecutorService swapper = Executors.newScheduledThreadPool(1);
        // swapper.scheduleAtFixedRate(() -> {
        //     state = !state;
        // }, 0, 3000, TimeUnit.MILLISECONDS);
    }

    private final TorqueTimeout t = new TorqueTimeout(5);

    private final void init() {
    }

    private final void update() {
        System.out.println(t.calculate(action()));

        //System.out.printf("State: %b, Cals: %b\n", state, click.calculate(state));

    }

}
