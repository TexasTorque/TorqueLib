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
    // We are just adding keys we have used to a hashmap so we can check
    // if we have already used them in O(1) time instead of O(n).
    // The value and type of the entry doesnt matter so we will just
    // be adding "true" values.
    private final HashMap<String, NetworkTableEntry> keys; 

    public TorqueLog(final String title) {
        this.title = title;
        // tab = Shuffleboard.getTab(title);
        tab = null;
        keys = new HashMap<String, NetworkTableEntry>();
    }



    // public void log(final String key, final Object value, final int width, final int height, final WidgetType type) {
     
    //     if (!keys.containsKey(key)) {
    //         final NetworkTableEntry entry = tab.add(key, value).withWidget(type).withSize(0, 0).withPosition(0, 0).getEntry();
    //         x += 
    //         keys.put(key, entry);
    //         return;
    //     }
    // }



    
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

    public Position calculatePosition(int width, int height) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.println("Checking " + i + ", " + j);
                if (!board[i][j]) {
                    System.out.println("Found empty space at " + i + ", " + j);
                    if (elementWillFit(i, j, width, height)) {
                        for (int k = 0; k < height; k++) {
                            for (int l = 0; l < width; l++) {
                                board[i + k][j + l] = true;
                            }
                        }
                        return new Position(i, j);
                    } 
                }
            }
        }
        return Position.INVALID;
    }

    private static class Position {
        public final int x, y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public static final Position INVALID = new Position(-1, -1);
    }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print((board[i][j] ? 'X' : '_') + " ");
            }
            System.out.println();
        }
    }
    

    public static void main(final String[] args) {
        TorqueLog log = new TorqueLog("test");
        log.calculatePosition(2, 2);
        log.calculatePosition(2, 1);
        log.calculatePosition(3, 1);
        log.calculatePosition(4, 3);
        log.calculatePosition(1, 1);
        log.calculatePosition(3, 2);
        log.calculatePosition(1, 2);
        log.printBoard();
    }




}
