package org.texastorque.torquelib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parameters {

    private final File paramsFile;
    private final ArrayList<Constant> constants;

    /*add any hardcoded constants here*/
    private final Constant[] overriddenConstant = {};

    /**
     * Make a new Parameters loader.
     *
     * @param filePath String path of parameters file.
     */
    public Parameters(String filePath) {
        paramsFile = new File(filePath);
        try {
            paramsFile.createNewFile();
        } catch (IOException e) {
        }

        constants = new ArrayList<>();
    }

    /**
     * Load the parameters file using this syntax:<br><br>
     *
     * nameOfParameter valueOfParameter~useFileConstant<br>
     * shooterMotorLeft 2~false<br><br>
     *
     * When useFileConstant is true, the file's constant will override the
     * hardcoded constant.
     */
    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(paramsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                int pos = line.indexOf(" ");
                int useFileConstant = line.indexOf("~");
                if (pos != -1) {
                    constants.add(makeConstant(line.substring(0, pos), line.substring(pos, useFileConstant), line.substring(useFileConstant)));
                } else {
                    System.err.println("Could not read a constant.");
                }
            }
        } catch (Exception e) {
        }
    }

    private Constant makeConstant(String key, String line, String useFile) {
        for (Constant oC : overriddenConstant) {
            if (oC.getKey().equals(key) && Boolean.parseBoolean(useFile)) {
                return oC;
            }
        }

        if (line.contains(".")) {//decimal
            if (line.contains("f")) {//float
                return new Constant(key, Float.parseFloat(line));
            } else {//not float
                return new Constant(key, Double.parseDouble(line));
            }
        } else if (line.contains("true") || line.contains("false")) {//boolean
            return new Constant(key, (line.contains("false") ? 0 : 1));
        } else {//nothing else
            return new Constant(key, Integer.parseInt(line));
        }
    }

    public class Constant {

        private final String key;
        private final Number value;

        private final boolean finalValue;

        /**
         * Make a final Constant. Should only be used in overridden constants
         * array.
         *
         * @param key Name of value.
         * @param value Value.
         */
        public Constant(String key, Number value) {
            this.key = key;
            this.value = value;

            finalValue = true;
        }

        public float getFloat() {
            return value.floatValue();
        }

        public boolean getBoolean() {
            return value.intValue() == 1;
        }

        public int getInt() {
            return value.intValue();
        }

        public double getDouble() {
            return value.doubleValue();
        }

        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }
}
