package org.texastorque.torquelib.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TorqueLogging {

    private static TorqueLogging instance;

    private BufferedWriter fileIO = null;

    private String fileName = "TorqueLog.csv";
    private String filePath = "file:///ni-rt/startup/";
    private File logFile;

    private String keyNames;
    private String logString;

    private boolean logToDashboard;

    public static TorqueLogging getInstance() {
        return (instance == null) ? instance = new TorqueLogging() : instance;
    }

    private TorqueLogging() {
        try {
            logFile = new File(filePath + fileName);
            fileIO = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            System.err.println("Error creating file in TorqueLogging.");
        }

        keyNames = null;
        logString = null;
        logToDashboard = false;
    }

    public void setDashboardLogging(boolean dashLog) {
        logToDashboard = dashLog;
    }

    public void createNewFile() {
        if (logFile.exists()) {
            logFile.delete();
        }
        instance = new TorqueLogging();
    }

    public void logKeyNames(String names) {
        keyNames = names;
        log(keyNames);
    }

    public void logData(String data) {
        logString = data;
        log(logString);
    }

    private void log(String str) {
        if (logToDashboard) {
            SmartDashboard.putString("TorqueLogging", str);
        } else {
            try {
                fileIO.write(str);
                fileIO.newLine();
                fileIO.flush();
            } catch (IOException e) {
                System.err.println("Error logging some data.");
            }
        }
    }

}
