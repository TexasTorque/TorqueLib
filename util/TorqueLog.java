package org.texastorque.torquelib.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;

/**
 * A wrapper around a shuffleboard tab that allows for widgets to be added 
 * and updated with a single call in the update function. It also handles auto
 * positioning of widgets on the board with a given size.
 * 
 * @author Justus Languell
 */
public final class TorqueLog {
    public static final int ROWS = 6, COLUMNS = 10;
    private final boolean[][] board = new boolean[ROWS][COLUMNS];

    public final String title;
    private final ShuffleboardTab tab;
    private final Map<String, NetworkTableEntry> keys; 
    private final List<ShuffleboardComponent<?>> initialEntries;

    /**
     * Create a new logging table.
     * 
     * @param title The title of the table on the dashboard.
     */
    public TorqueLog(final String title) {
        this.title = title;
        tab = Shuffleboard.getTab(title);
        initialEntries = tab.getComponents();
        keys = new HashMap<String, NetworkTableEntry>();
    }

    /**
     * Log a value to the dashboard with properties.
     * 
     * @param key The title of the widget.
     * @param value The value to log in the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     * @param type The type of the cell.
     * @param properties The properties of the widget.
     */
    public void log(final String key, final Object value, final int width, final int height, 
            final WidgetType type, final Map<String, Object> properties) {
        if (!keys.containsKey(key)) {
          
            final Position pos = calculatePosition(width, height);
            if (pos == Position.INVALID) {
                System.out.println("Could not fit element " + key + " in log " + title);
                return;
            }
            SimpleWidget widget = tab.add(key, value)
                    .withWidget(type)
                    .withSize(width, height)
                    .withPosition(pos.x, pos.y);

            if (properties != null && !properties.isEmpty())
                widget = widget.withProperties(properties);

            keys.put(key, widget.getEntry());
            return;
        }
        keys.get(key).setValue(value);
    }

    /**
     * Log a value to the dashboard without properties.
     * 
     * @param key The title of the widget.
     * @param value The value to log in the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     * @param type The type of the cell.
     */
    public void log(final String key, final Object value, final int width, final int height, final WidgetType type) {
        log(key, value, width,  height, type, null);
    }

    /**
     * Log a boolean value to the dashboard and infer the widget type.
     * 
     * @param key The title of the widget.
     * @param value The value to log in the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     */
    public void log(final String key, final boolean value, final int width, final int height) {
        log(key, value, width, height, BuiltInWidgets.kBooleanBox);
    }

    /**
     * Log a string value to the dashboard and infer the widget type.
     * 
     * @param key The title of the widget.
     * @param value The value to log in the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     */
    public void log(final String key, final String value, final int width, final int height) {
        log(key, value, width, height, BuiltInWidgets.kTextView);
    }

    /**
     * Find the next coordinate location that a widget of size (width, height) can fit onto the dashboard.
     * 
     * This algorithm is definitely not the most efficient because it kind of checks every position
     * on the board. However, it is simple and works well enough for our purposes. Since it only runs once
     * on the constructor with a very small size it doesn't matter at all.
     * 
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     * 
     * @return The next valid position or INVALID if there isn't a valid position.
     */
    private Position calculatePosition(final int width, final int height) {
        // for (int i = 0; i < board.length - height; i++)
        //     for (int j = 0; j < board[i].length - width; j++) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j]) continue;
                if (!widgetWillFit(i, j, width, height)) continue;
                markWidgetTaken(i, j, width, height);
                return new Position(i, j);
            }
        return Position.INVALID;
    }

    /**
     * Check if an widget of size (width, height) will fit at the location (x, y).
     * 
     * @param x The x location of the widget.
     * @param y The y location of the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     * 
     * @return If the widget will fit or not.
     */
    private boolean widgetWillFit(final int x, final int y, final int width, final int height) {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (i + x >= board.length || j + y >= board[i].length)
                    return false;
                if (board[i + x][j + y])
                    return false;
            }
        return true;
    }

    /**
     * Mark the cells covered by an widget of size (width, height) at the location (x, y) as taken.
     * 
     * @param x The x location of the widget.
     * @param y The y location of the widget.
     * @param width The width of the widget in cells.
     * @param height The height of the widget in cells.
     */
    private void markWidgetTaken(final int x, final int y, final int width, final int height) {
         for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++)
                board[x + i][y + j] = true;
    }

    /**
     * A class to represent a 2d position on the first quadrent of the cartesian plane.
     */
    private static class Position {
        public final int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public static final Position INVALID = new Position(-1, -1);
    }
 

    public static final WidgetType W_TEXT = BuiltInWidgets.kTextView;
    public static final WidgetType W_BOOL = BuiltInWidgets.kBooleanBox;
    public static final WidgetType W_DIAL = BuiltInWidgets.kDial;
    public static final WidgetType W_GRAPH = BuiltInWidgets.kGraph;
    public static final WidgetType W_SLIDER = BuiltInWidgets.kNumberSlider;
    public static final WidgetType W_BAR = BuiltInWidgets.kNumberBar;

    /**
    * Custom properties:
    *
    * <table>
    * <tr><th>Name</th><th>Type</th><th>Default Value</th><th>Notes</th></tr>
    * <tr><td>Major tick spacing</td><td>Number</td><td>45</td><td>Degrees</td></tr>
    * <tr><td>Starting angle</td><td>Number</td><td>180</td>
    * <td>How far to rotate the entire dial, in degrees</td></tr>
    * <tr><td>Show tick mark ring</td><td>Boolean</td><td>true</td></tr>
    * </table>
    */
    public static final WidgetType W_GYRO = BuiltInWidgets.kGyro;
}
