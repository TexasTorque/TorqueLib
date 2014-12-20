package org.texastorque.torquelib.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlashLogger {

    private FileWriter writer;
    
    private final String filePath = "/media/sda1/logging/";
    private String fileName;

    /**
     * Create a new flash drive logger.
     *
     * @param filePath File path of logging file in flash drive.
     */
    public FlashLogger() {
        reset();
    }
    
    public void reset()
    {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM--dd::HH--mm", Locale.US);
        String time = format.format(currentDate);
        fileName = "log" + time;
        
        File file = new File(filePath + fileName);
        try {
            file.createNewFile();
            writer = new FileWriter(file, true);
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
        
        try {
            writer.flush();
        } catch (IOException ex) {
            System.err.println("IOException in flushing.");
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("IOException in closing flash logger.");
        }
    }
}
