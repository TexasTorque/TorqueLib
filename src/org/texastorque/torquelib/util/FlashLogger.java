package org.texastorque.torquelib.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class FlashLogger {

    private BufferedWriter writer;

    private String filePath;
    private String fileName;

    private boolean enabled;
    private boolean firstWrite;

    private ArrayList<Loggable> loggedSystems;

    /**
     * Create a new flash drive logger.
     *
     */
    public FlashLogger() {
        this("/media/sda1/logging/");
    }

    public FlashLogger(String path) {
        filePath = path;
        loggedSystems = new ArrayList<>();
        reset();
    }

    /**
     * Make a new file and reset the logging system.
     *
     */
    public void reset() {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yy_HH-mm", Locale.US);
        String time = format.format(currentDate);
        fileName = "LOG " + time + ".csv";

        try {
            File file = new File(filePath + fileName);
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.err.println("IOException in creating flash logger.");
        }

        enabled = false;
        firstWrite = true;
    }

    /**
     * Log data from the systems registered for logging.
     *
     */
    public void log() {
        if (enabled) {
            try {
                if (firstWrite) {
                    String names = "";
                    for (Loggable l : loggedSystems) {
                        names += l.getLogNames();
                    }
                    writer.write(names + "\n");
                    firstWrite = false;
                } else {
                    String vals = "";
                    for (Loggable l : loggedSystems) {
                        vals += l.getLogValues();
                    }
                    writer.write(vals + "\n");
                }

            } catch (IOException e) {
                System.err.println("IOException in writing to file.");
            }

            try {
                writer.flush();
            } catch (IOException ex) {
                System.err.println("IOException in flushing.");
            }
        }
    }

    /**
     * Close the file after a logging session is completed.
     *
     */
    public void close() {
        enabled = false;
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                System.err.println("IOException in closing filewriter.");
            }
        }
    }

    /**
     * Register a new system for logging.
     *
     * @param log The new system to be logged.
     */
    public void addLoggable(Loggable log) {
        if (!loggedSystems.contains(log)) {
            loggedSystems.add(log);
        }
    }

    public void enable() {
        enabled = writer != null;
    }

    public void disable() {
        enabled = false;
    }
}
