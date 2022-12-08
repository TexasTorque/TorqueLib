package org.texastorque.torquelib.util;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.WidgetType;

public final class TorqueLog {

    public static final int ROWS = 6, COLUMNS = 10;
    private final boolean[][] board = new boolean[ROWS][COLUMNS];

    public final String title;
    private final ShuffleboardTab tab;
    private final HashMap<String, NetworkTableEntry> keys; 

    public TorqueLog(final String title) {
        this.title = title;
        tab = Shuffleboard.getTab(title);
        keys = new HashMap<String, NetworkTableEntry>();
    }

    public void log(final String key, final Object value, final int width, final int height, final WidgetType type) {
        if (!keys.containsKey(key)) {
            final Position pos = calculatePosition(width, height);
            if (pos == Position.INVALID) {
                System.out.println("Could not fit element " + key + " in log " + title);
                return;
            }
            final NetworkTableEntry entry = tab.add(key, value)
                    .withWidget(type)
                    .withSize(width, height)
                    .withPosition(pos.x, pos.y)
                    .getEntry();
            keys.put(key, entry);
            return;
        }
        keys.get(key).setValue(value);
    }
 
    
    private Position calculatePosition(int width, int height) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j]) continue;
                if (!elementWillFit(i, j, width, height)) continue;
                markElementsTaken(i, j, width, height);
                return new Position(i, j);
            }
        return Position.INVALID;
    }

    private boolean elementWillFit(final int x, final int y, final int width, final int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i + x >= board.length || j + y >= board[i].length)
                    return false;
                if (board[i + x][j + y])
                    return false;
            }
        }
        return true;
    }

    private void markElementsTaken(final int i, final int j, final int width, final int height) {
         for (int k = 0; k < height; k++)
            for (int l = 0; l < width; l++)
                board[i + k][j + l] = true;
    }

    private static class Position {
        public final int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public static final Position INVALID = new Position(-1, -1);
    }

    public static void main(final String[] args) {
    }

 
    // This is an example of unhealthy nesting... 6 whole layers!!
    // This was the original state of the calculatePosition method.
    // The rule of thumb is after 3 layers of nesting YOU MUST refactor.
    // You can refactor through:
    // 1. Extracting into functions (ie. elementsWillFit, markElementsTaken)
    // 2. Inverting conditions with early return, continue, break (validation checking) 
    // public Position calculatePosition(int width, int height) {
    //     for (int i = 0; i < board.length; i++) {                     // layer 1
    //         for (int j = 0; j < board[i].length; j++) {              // layer 2
    //             if (!board[i][j]) {                                  // layer 3
    //                 if (elementWillFit(i, j, width, height)) {       // layer 4
    //                     for (int k = 0; k < height; k++) {           // layer 5
    //                         for (int l = 0; l < width; l++) {        // layer 6
    //                             board[i + k][j + l] = true;
    //                         }
    //                     }
    //                     return new Position(i, j);
    //                 } 
    //             }
    //         }
    //     }
    //     return Position.INVALID;
    // }
}
