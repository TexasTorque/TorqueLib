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
    
    public Parameters()
    {
        this("/home/admin/params.txt");
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
                            c.value = Double.parseDouble(line.substring(pos));
                        }
                    }
                } else {
                    System.out.println("Invalid line");
                }
            }
        } catch (Exception e) {
            System.out.println("Messed up readin constants");
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

            constants.add(this);
        }

        public String getKey() {
            return key;
        }
        
        public Double getDouble()
        {
            return value;
        }
        
        public boolean getBoolean()
        {
            return value == 1;
        }

        @Override
        public String toString() {
            return key + ": " + value;
        }
    }
}
