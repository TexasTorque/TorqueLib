package org.texastorque.torquelib.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

public class FlashLogger {

    private FileWriter writer;

    /**
     * Create a new flash drive logger with detected flash drive. Work in
     * progress.
     */
    public FlashLogger() {
        String filePath = "";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        String fileName = "";
        File file = new File(filePath + fileName);
        try {
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (IOException e) {
            System.err.println("IOException in creating flash logger.");
        }
    }

    /**
     * Create a new flash drive logger.
     *
     * @param filePath File path of logging file in flash drive.
     */
    public FlashLogger(String filePath) {
        File file = new File(filePath);
        try {
            file.createNewFile();
            writer = new FileWriter(file);
        } catch (IOException e) {
            System.err.println("IOException in creating flash logger.");
        }
    }

    public void log(String msg) {
        try {
            writer.write(msg);
        } catch (IOException e) {
            System.err.println("IOException in writing to flash logger.");
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("Error in closing flash logger.");
        }
    }
}
