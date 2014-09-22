package org.texastorque.torquelib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class Parameters {

    private static Parameters teleopInstance;
    private static Parameters autonInstance;
    private static Parameters visionInstance;
    private Hashtable map;

    private String fileName;
    private String filePath;
    private File parametersFile;

    private BufferedReader fileIO = null;

    public synchronized static Parameters getTeleopInstance() {
        return (teleopInstance == null) ? teleopInstance = new Parameters("params.txt") : teleopInstance;
    }

    public synchronized static Parameters getAutonInstance() {
        return (autonInstance == null) ? autonInstance = new Parameters("autonParams.txt") : autonInstance;
    }

    public synchronized static Parameters getVisionInstance() {
        return (visionInstance == null) ? visionInstance = new Parameters("params.txt", "C:/Users/Texas Torque/Desktop/Params/") : visionInstance;
    }

    public Parameters(String fileNm) {
        map = new Hashtable();
        filePath = "file:///ni-rt/startup/";
        fileName = fileNm;

        parametersFile = new File(filePath + fileName);
    }

    public Parameters(String fileNm, String path) {
        map = new Hashtable();
        filePath = path;
        fileName = fileNm;

        parametersFile = new File(filePath + fileName);
    }

    public void load() {
        try {
            clearList();
            if (parametersFile.exists()) {
                fileIO = new BufferedReader(new FileReader(parametersFile));
                String line;
                int index;
                while ((line = fileIO.readLine()) != null) {
                    if (!line.equals("")) {
                        map.put(line.substring(0, index = line.indexOf(" ")), line.substring(index + 1));
                    }
                }
                System.err.println("Parameters file: " + fileName + " successfully loaded.");
                fileIO.close();
            } else {
                System.err.println("Could not load parameters file: " + fileName);
            }
        } catch (IOException e) {
            System.err.println("IOException caught trying to read in parameters file.");
        }
    }

    public synchronized int getAsInt(String name, int dflt) {
        String value = (String) map.get(name);
        return (value == null) ? dflt : (int) Double.parseDouble(value);
    }

    public synchronized double getAsDouble(String name, double dflt) {
        String value = (String) map.get(name);
        return (value == null) ? dflt : Double.parseDouble(value);
    }

    public synchronized boolean getAsBoolean(String name, boolean dflt) {
        String value = (String) map.get(name);
        return (value == null) ? dflt : Integer.parseInt(value) != 0;
    }

    private void clearList() {
        map.clear();
    }
}
