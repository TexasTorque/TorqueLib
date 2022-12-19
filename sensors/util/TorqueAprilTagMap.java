/**
 * Copyright 2011-2022 Texas Torque.
 * 
 * This file is part of TorqueLib, which is licensed under the MIT license.
 * For more details, see ./license.txt or write <jus@justusl.com>.
 */
package org.texastorque.torquelib.sensors.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.Filesystem;

/**
 * Encapsulates a map of AprilTags and their positions by ID. Encorporates deserializing from JSON.
 * 
 * @author Justus Languell
 */
public final class TorqueAprilTagMap extends HashMap<Integer, Pose3d> {

    public static final String DEFAULT_FILENAME = "AprilTags.json";

    private TorqueAprilTagMap() { super(); }

    /**
     * Deserializes a TorqueAprilTagMap from a JSON file from default filename "AprilTags.json".
     * 
     * @return The deserialized TorqueAprilTagMap.
     */
    public static final TorqueAprilTagMap fromJSON() {
        return fromJSON(DEFAULT_FILENAME);
    }

    /**
     * Deserializes a TorqueAprilTagMap from a JSON file from a filename.
     * 
     * @return The deserialized TorqueAprilTagMap.
     */
    public static final TorqueAprilTagMap fromJSON(final String path) {
        final TorqueAprilTagMap map = new TorqueAprilTagMap();
        final JSONObject json = readJson(path);
        final Iterator<?> keys = json.keySet().iterator();

        while (keys.hasNext()) {
            final String key = (String) keys.next();
            final JSONObject tag = (JSONObject) json.get(key);
            final Translation3d translation = deserializeTranslation((JSONArray) tag.get("t"));
            final Rotation3d rotation = deserializeRotation((JSONArray) tag.get("r"));
            map.put(Integer.parseInt(key), new Pose3d(translation, rotation));
        }

        return map;
    }

    private static final Translation3d deserializeTranslation(final JSONArray array) {
        final double x = Double.parseDouble("" + array.get(0));
        final double y = Double.parseDouble("" + array.get(1));
        final double z = Double.parseDouble("" + array.get(2));
        return new Translation3d(x, y, z);
    }

    private static final Rotation3d deserializeRotation(final JSONArray array) {
        final double roll = Double.parseDouble("" + array.get(0));
        final double pitch = Double.parseDouble("" + array.get(1));
        final double yaw = Double.parseDouble("" + array.get(2));
        return new Rotation3d(Math.toRadians(roll), Math.toRadians(pitch), Math.toRadians(yaw));
    }

    private static final JSONObject readJson(final String path) {
        final File fileToRead = new File(Filesystem.getDeployDirectory(), path);
        try (final BufferedReader br = new BufferedReader(new FileReader(fileToRead))) {
            final StringBuilder fileContentBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                fileContentBuilder.append(line);
            String fileContent = fileContentBuilder.toString();
            return (JSONObject) new JSONParser().parse(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
