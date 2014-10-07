package org.texastorque.torquelib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parameters {

    public static ArrayList<Constant> constants;

    private final File paramsFile;

    /*declare constants here*/
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
     * nameOfParameter valueOfParameter<br>
     * shooterMotorLeft 2<br><br>
     *
     * Constants listed in the file override hardcoded constants.
     */
    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(paramsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                int pos = line.indexOf(" ");
                if (pos != -1) {
                    for (Constant c : constants) {
                        if (c.getKey().equals(line.substring(0, pos))) {
                            c.setValue(Double.parseDouble(line.substring(pos)));
                        }
                    }
                } else {
                    System.err.println("Could not read a constant.");
                }
            }
        } catch (Exception e) {
        }
    }

    public static class Constant {

        private final String key;
        private double value;

        /**
         * Make a final Constant.
         *
         * @param key Name of value.
         * @param value Value.
         */
        public Constant(String key, double value) {
            this.key = key;
            this.value = value;

            add();//to take care of "leaking this in constructor" warning
        }

        private void add() {
            constants.add(this);
        }

        public boolean getBoolean() {
            return value == 1;
        }

        public double getDouble() {
            return value;
        }

        public String getKey() {
            return key;
        }
        
        protected void setValue(double val)
        {
            value = val;
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }
}
