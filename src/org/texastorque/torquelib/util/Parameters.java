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
                            c = makeConstant(line.substring(0, pos), line.substring(pos));
                        }
                    }
                } else {
                    System.err.println("Could not read a constant.");
                }
            }
        } catch (Exception e) {
        }
    }

    private Constant makeConstant(String key, String line) {
        if (line.contains(".")) {//decimal
            return new Constant(key, Double.parseDouble(line));
        } else if (line.contains("true") || line.contains("false")) {//boolean
            return new Constant(key, (line.contains("false") ? 0 : 1));
        } else {//nothing else
            return new Constant(key, Integer.parseInt(line));
        }
    }

    public class Constant {

        private final String key;
        private final Number value;

        /**
         * Make a final Constant.
         *
         * @param key Name of value.
         * @param value Value.
         */
        public Constant(String key, Number value) {
            this.key = key;
            this.value = value;

            add();//to take care of "leaking this in constructor" warning
        }

        private void add() {
            constants.add(this);
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
