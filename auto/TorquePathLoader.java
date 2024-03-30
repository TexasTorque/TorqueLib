package org.texastorque.torquelib.auto;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.pathplanner.lib.path.PathPlannerPath;

/**
 * Preloads paths to avoid calling the expensive PathPlannerPath.loadPathFile 
 * during auto runtime. 
 */
public final class TorquePathLoader {
    private final Map<String, PathPlannerPath> loadedTrajectories = new HashMap<>();

    public TorquePathLoader() {}

    public void preloadPath(final String pathName) {
        if (loadedTrajectories.containsKey(pathName)) {
            return;
        }
        loadedTrajectories.put(pathName, PathPlannerPath.fromPathFile(pathName));
    }

     public void preloadPathSafe(final String pathName) {
        if (loadedTrajectories.containsKey(pathName)) {
            return;
        }
        try {
            final PathPlannerPath loadedPath = PathPlannerPath.fromPathFile(pathName)
            loadedTrajectories.put(pathName, loadedPath);
        } catch (final FileNotFoundException e) {
            System.out.println("Failed to load path " + pathName);
        }
    }

    public Optional<PathPlannerPath> getPathSafe(final String pathName) {
        if (!loadedTrajectories.containsKey(pathName)) {
            return Optional.empty();
        }
        return Optional.of(getPathUnsafe(pathName));
    }

    public PathPlannerPath getPathSlow(final String pathName) {
        if (!loadedTrajectories.containsKey(pathName)) {
            return PathPlannerPath.fromPathFile(pathName);
        }
        return getPathUnsafe(pathName);
    }

    public PathPlannerPath getPathUnsafe(final String pathName) {
        return loadedTrajectories.get(pathName);
    }
}
