import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
// java.util.Queue and java.util.LinkedList are not used if using custom Queue
// import java.util.LinkedList;
// import java.util.Queue;

public class Map implements ActionListener {
    double v;
    double[] vy = {0, 0};
    double v_normal;
    double v_slow;
    double[] vx = {0, 0};
    int width, height;
    double x, y;
    int size;
    int Menusize;
    ArrayList<Gate> gates = new ArrayList<Gate>();
    ArrayList<Gate> gatesFake = new ArrayList<Gate>();
    int counter = 1;
    boolean allOpen = false;
    static boolean damage = false;
    static int invinc;
    int eX = 1, eY;
    int prev = 1;
    boolean gothoughwalls = false;

    Timer powerupTimer;
    int POWERUPSPEED = 1;
    int powerupcount = 0;

    int[][] map;
    int[][] enemyMap;
    boolean payGate = false;
    boolean openGate = false;
    boolean moving = false;
    Color circuitColor = Color.BLUE;
    boolean GateCollision = false;

    // Helper class for BFS state
    static class BFSNode {
        int x, y; // column, row
        BFSNode parent;

        BFSNode(int x, int y, BFSNode parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }

    Map(double x, double y, int size, double v, int[][] map) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.v = v;
        this.map = map;
    }

    Map(double x, double y, int size, double v, int mapLevel) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.Menusize = 4;
        this.v = v * this.size;
        /*
         * 0 = wall (was empty in one comment, wall in another, assuming 0 is wall based on usage)
         * 1 = empty path
         * 2 = gate
         * 3 = circuit
         * 4 = actual gate (from file loading)
         * 5 = "dumb" gate (from file loading)
         * 7 = enemy (basic)
         * 8 = BFS enemy
         * 9 = smart enemy
         * 10 = cookie
         */
        this.map = read_map_from_file("" + mapLevel); // Ensure read_map_from_file is robust

        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 4) { // Gate type from file
                    gates.add(new Gate(r, c, this.size,Math.round((long)(Math.random()*10))));
                } else if (this.map[c][r] == 5) { // Fake Gate type from file
                    gatesFake.add(new Gate(r, c, this.size,Math.round((long)(Math.random()*10))));
                }
            }
        }
    }

    Map(double x, double y, int size, double v) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.Menusize = 4;
        this.v = v * this.size;
        /*
         * 0 = wall
         * 1 = empty path
         * 2 = gate (general, might be used for drawing if not 4/5)
         * 3 = circuit
         * 4 = actual gate (specific type for logic)
         * 5 = "dumb" gate (specific type for logic)
         * 7 = enemy (basic)
         * 8 = BFS enemy
         * 9 = smart enemy
         * 10 = cookie
         */
        this.map = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 8, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        this.enemyMap = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 8, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 4, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 4) { // Gate type from file
                    gates.add(new Gate(r, c, this.size,Math.round((long)(Math.random()*10))));
                } else if (this.map[c][r] == 5) { // Fake Gate type from file
                    gatesFake.add(new Gate(r, c, this.size,Math.round((long)(Math.random()*10))));
                }
            }
        }
    }

    private static int[] convert_to_int(String[] arr) {
        int[] int_arr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int_arr[i] = Integer.parseInt(arr[i]);
        }
        return int_arr;
    }

    static int[][] read_map_from_file(String filename) {
        File textFile = new File("src/Levels/" + filename +".txt");
        String lineOfText;
        try {
            FileReader in = new FileReader(textFile);
            BufferedReader readFile = new BufferedReader(in);
            lineOfText = readFile.readLine();
            int [][] readmap = new int[lineOfText.split(" ").length][lineOfText.split(" ").length];
            int count = 0;
            readmap[count] = new int[lineOfText.split(" ").length];
            while ((lineOfText = readFile.readLine()) != null){
                count ++;
                readmap[count] = convert_to_int(lineOfText.split(" "));
            }
            readFile.close();
            in.close();
            for(int i = 0; i < readmap.length; i++){
                String line  = "";
                for(int j = 0; j < readmap[i].length; j++){
                    line += readmap[j][i] + " ";
                }
                System.out.println(line);
            }
            return readmap;
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist or could not be found.");
            System.err.println("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Problem reading file.");
            System.err.println("IOException: " + e.getMessage());
        }
        return null;
    }

    void mapScrub() {
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 3) {
                    this.map[c][r] = 1;
                }
            }
        }
    }

    void moveup(){
        this.vy[0] = -this.v;
    }
    void movedown(){
        this.vy[1] =this.v;
    }
    void moveleft(){
        this.vx[0] = -this.v;
    }
    void moveright(){
        this.vx[1] =this.v;
    }
    void move(Player player){
        if (!moving){
            boolean stop = false;
            if(!this.gothoughwalls){
                for(Rectangle wall : getWalls()) {
                    //if (player.getrect().intersects(wall)) {
                    this.checkCollision(player, wall);
                }
            }
            if (!stop){
                this.x += this.vx[0] + this.vx[1];
                this.y += this.vy[0] + this.vy[1];
            }

            //Check material collision(need to change to collisin like above
            for (int c = 0; c < this.map.length; c++){
                for (int r = 0; r < this.map[c].length; r++){
                    if (this.map[c][r] == 2){
                        Rectangle tempr = new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size);
                        if(player.getrect().intersects(tempr)){
                            map[c][r] = 1;
                            player.addInventory();
                        }
                    }
                }
            }

            for(Square powerup: getSquares(10)){
                if(player.getrect().intersects(powerup)){
                   map[powerup.yNum][powerup.xNum] = 1;
                   powerupTimer= new Timer(POWERUPSPEED, this);
                   powerupTimer.start();
                   int randint = (int) (Math.random() * 4);
                   if (randint == 0){
                       this.v += 3;
                       System.out.println("speed up");
                   }else if (randint == 1){
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       player.addInventory();
                       System.out.println("Inventory up");
                   } else if (randint == 2){
                       this.gothoughwalls = true;
                       System.out.println("Gothoughwalls");
                   }else if (randint == 3){
                       invinc = 1;
                       System.out.println("Invincibility");
                   }
                }
            }

            this.GateCollision = false;
            for(Gate gate: gatesFake) {
                //if (player.getrect().intersects(wall)) {
                this.checkCollision(player, gate.getrect(this.x, this.y, this.size));
                if(this.checkInRect(player, gate.getCollisionRect(this.x, this.y, this.size))){
                    this.GateCollision = true;
                    if (this.payGate){
                        player.removeInventory();
                        this.payGate = false;
                        gate.working = true;
                    }
                    else if(this.openGate){
                        this.openGate = false;
                        if (gate.open){
                            gate.open = false;
                        }else{
                            gate.open = true;
                        }
                    }
                }
            }
            for(Gate gate: gates) {
                this.checkCollision(player, gate.getrect(this.x, this.y, this.size));
                if(this.checkInRect(player, gate.getCollisionRect(this.x, this.y, this.size))){
                    this.GateCollision = true;
                    if (this.payGate){
                        player.removeInventory();
                        this.payGate = false;
                        gate.working = true;
                    }
                    else if(this.openGate){
                        this.openGate = false;
                        if (gate.open){
                            gate.open = false;
                        }else{
                            gate.open = true;
                        }
                    }
                }
            }
            allOpen = true;
            for(Gate gate: gates) {
                if (!gate.working || !gate.open){
                    allOpen = false;
                }
            }
            if (allOpen){
                circuitColor = Color.CYAN;
            }
            for (int c = 0; c < this.map.length; c++){
                for (int r = 0; r < this.map[c].length; r++){
                    if (this.map[c][r] == 1){
                        Rectangle tempr = new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size);
                        if(player.getView().intersects(tempr)){
                            map[c][r] = 5;
                        }
                    }
                }
            }
            for (int c = 0; c < this.map.length; c++){
                for (int r = 0; r < this.map[c].length; r++){
                    if (this.map[c][r] == 11){
                        Rectangle tempr = new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size);
                        if(player.getrect().intersects(tempr)){
                            this.moving = true;
                        }
                    }
                }
            }
        }else{
            this.moveTo(0, 0);
        }
    }
    void moveTo(int x, int y){
        this.moving = true;
        if (this.x < x){
            this.x += 1;
        }
        else if (this.x > x){
            this.x -= 1;
        }
        if (this.y < y){
            this.y += 1;
        }
        else if(this.y > y){
            this.x -= 1;
        }
        if(Math.round(this.y) == y && Math.round(this.x) == x){
            this.moving = false;
            System.out.println(this.x + " " + this.y);
            map[20][20] = 7;
        }
    }

    ArrayList<Rectangle> getOpen() {
        ArrayList<Rectangle> open = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 1) {
                    open.add(new Rectangle((int) (r * size + this.x), (int) (c * size + this.y), size, size));
                }
            }
        }
        return open;
    }

    ArrayList<Rectangle> getWalls() {
        ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 0) {
                    walls.add(new Rectangle((int) (r * size + this.x), (int) (c * size + this.y), size, size));
                }
            }
        }
        return walls;
    }

    ArrayList<Rectangle> getCircuit() {
        ArrayList<Rectangle> circuit = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 3) {
                    circuit.add(new Rectangle((int) (r * size + this.x), (int) (c * size + this.y), size, size));
                }
            }
        }
        return circuit;
    }

    ArrayList<Rectangle> getMaterials() {
        ArrayList<Rectangle> materials = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 10) { // Assuming 10 is material/cookie
                    materials.add(new Rectangle((int) (r * size + this.x), (int) (c * size + this.y), size, size));
                }
            }
        }
        return materials;
    }

    ArrayList<Square> getSquares(int[][] map, int code) {
        ArrayList<Square> squares = new ArrayList<Square>();
        for (int c = 0; c < map.length; c++) {
            for (int r = 0; r < map[c].length; r++) {
                if (map[c][r] == code) {
                    squares.add(new Square((int) (r * size + this.x), (int) (c * size + this.y), size, size, r, c));
                }
            }
        }
        return squares;
    }
    ArrayList<Square> getSquares(int code) {
        ArrayList<Square> squares = new ArrayList<Square>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == code) {
                    squares.add(new Square((int) (r * size + this.x), (int) (c * size + this.y), size, size, r, c));
                }
            }
        }
        return squares;
    }

    void updateGate() {
        allOpen = true;
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 3) {
                    allOpen = false;
                }
            }
        }
        if (allOpen) {
            for (Gate gate : gates) {
                this.map[gate.y][gate.x] = 1;
            }
        }
    }

    // Original BFS, likely incompatible with Queue<BFSNode> if not updated.
    // Kept for reference, but findNextStepBFS is used by new enemies.
    int[] bfs(int[] start, int[] end) {
        boolean[][] visited = new boolean[map.length][map[0].length];
        Queue queue = new Queue(map.length * map[0].length); // Custom Queue

        // This method needs to be adapted if it's to be used with Queue<BFSNode>
        // For now, it will cause issues if called directly because Queue expects BFSNode.
        // queue.enqueue(start); // ERROR: Queue expects BFSNode
        System.out.println("Warning: Original 'bfs' method is likely incompatible with current Queue implementation.");
        return null; // Returning null as it's not functional with current Queue
    }

    private int[] findNextStepBFS(int[] startCoords, int[] endCoords) {
        int startX = startCoords[0]; // column
        int startY = startCoords[1]; // row
        int endX = endCoords[0];     // column
        int endY = endCoords[1];     // row

        if (startX == endX && startY == endY) {
            return startCoords; // Already at destination
        }

        Queue queue = new Queue(map.length * map[0].length); // Use custom Queue
        boolean[][] visited = new boolean[map.length][map[0].length];

        queue.enqueue(new BFSNode(startX, startY, null));
        visited[startY][startX] = true;

        BFSNode destNode = null;

        int[] dCol = {0, 0, 1, -1}; // Change in col for neighbors (Right, Left)
        int[] dRow = {1, -1, 0, 0}; // Change in row for neighbors (Down, Up) - standard cartesian order often (0,1) (0,-1) (1,0) (-1,0)

        while (!queue.isEmpty()) {
            BFSNode current = queue.dequeue();

            if (current.x == endX && current.y == endY) {
                destNode = current;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int newCol = current.x + dCol[i];
                int newRow = current.y + dRow[i];

                if (newCol >= 0 && newCol < map[0].length && newRow >= 0 && newRow < map.length &&
                        !visited[newRow][newCol]) {

                    int cellType = map[newRow][newCol];
                    boolean isTargetCell = (newCol == endX && newRow == endY);
                    // Traversable if not a wall (0).
                    // Avoid other enemies (7, 8, 9) UNLESS it's the player's target cell.
                    boolean canTraverse = (cellType != 0); // Must not be a wall

                    if (canTraverse) {
                        if (!isTargetCell && (cellType == 7 || cellType == 8 || cellType == 9)) {
                            // Don't traverse intermediate cells occupied by other enemies
                            canTraverse = false;
                        }
                    }

                    if (canTraverse) {
                        visited[newRow][newCol] = true;
                        queue.enqueue(new BFSNode(newCol, newRow, current));
                    }
                }
            }
        }

        if (destNode == null) {
            return null; // No path found
        }

        BFSNode pathNode = destNode;
        if (pathNode.parent == null) { // Should only happen if start == end, handled at the beginning
            return startCoords;
        }

        while (pathNode.parent != null) {
            if (pathNode.parent.x == startX && pathNode.parent.y == startY) {
                // pathNode is the first step from startX, startY
                return new int[]{pathNode.x, pathNode.y};
            }
            pathNode = pathNode.parent;
        }
        return null; // Should ideally not be reached if a path was found
    }

    ArrayList<Enemy> getSmartEnemy() {
        ArrayList<Enemy> smartEnemiesList = new ArrayList<Enemy>();
        ArrayList<int[]> enemyGridPositions = new ArrayList<>();

        for (int row = 0; row < this.map.length; row++) {
            for (int col = 0; col < this.map[row].length; col++) {
                if (this.map[row][col] == 9) { // Smart enemy type
                    enemyGridPositions.add(new int[]{col, row});
                }
            }
        }

        if ((this.counter % 50) == 0) {
            for (int[] pos : enemyGridPositions) {
                int currentCol = pos[0];
                int currentRow = pos[1];

                if (this.map[currentRow][currentCol] != 9) {
                    continue;
                }

                double enemyScreenX = currentCol * size + this.x;
                double enemyScreenY = currentRow * size + this.y;

                int n_eX = 0;
                int n_eY = 0;

                if (enemyScreenX > 240) n_eX = -1;
                else if (enemyScreenX < 240) n_eX = 1;

                if (enemyScreenY > 240) n_eY = -1;
                else if (enemyScreenY < 240) n_eY = 1;

                if (currentCol + 1 < this.map[currentRow].length && this.map[currentRow][currentCol + 1] == 0) n_eX = -1;
                if (currentCol - 1 >= 0 && this.map[currentRow][currentCol - 1] == 0) n_eX = 1;
                if (currentRow + 1 < this.map.length && this.map[currentRow + 1][currentCol] == 0) n_eY = -1;
                if (currentRow - 1 >= 0 && this.map[currentRow - 1][currentCol] == 0) n_eY = 1;

                int targetCol = currentCol + n_eX;
                int targetRow = currentRow + n_eY;

                if (n_eX != 0 || n_eY != 0) {
                    if (targetRow >= 0 && targetRow < this.map.length && targetCol >= 0 && targetCol < this.map[targetRow].length) {
                        int contentOfTargetCell = this.map[targetRow][targetCol];
                        if (contentOfTargetCell != 0 && contentOfTargetCell != 7 && contentOfTargetCell != 8 && contentOfTargetCell != 9) {
                            this.map[targetRow][targetCol] = 9;
                            this.map[currentRow][currentCol] = 1; // Old spot becomes empty path
                        }
                    }
                }
            }
        }

        smartEnemiesList.clear(); // Re-populate after movement
        for (int row = 0; row < this.map.length; row++) {
            for (int col = 0; col < this.map[row].length; col++) {
                if (this.map[row][col] == 9) {
                    smartEnemiesList.add(new Enemy((int) (col * size + this.x), (int) (row * size + this.y), size, size, 0, 0, 9));
                }
            }
        }

        Rectangle playerHitbox = new Rectangle(240 - size / 2, 240 - size / 2, size, size);
        for (Enemy n : smartEnemiesList) {
            Rectangle enemyRect = new Rectangle(n.x, n.y, n.width, n.height);
            if (enemyRect.intersects(playerHitbox) && invinc == 0) {
                if (playerMotion.hp > 0) playerMotion.hp--; // Check if playerMotion is not null
                Map.damage = true;
            }
        }

        if (Map.damage) {
            invinc++;
            if (invinc >= 150) { // Use >= for safety
                invinc = 0;
                Map.damage = false;
            }
        }
        return smartEnemiesList;
    }

    ArrayList<Enemy> getBFSEnemy() {
        int playerScreenX = 240;
        int playerScreenY = 240;

        int playerGridCol = (int) ((playerScreenX - this.x) / this.size);
        int playerGridRow = (int) ((playerScreenY - this.y) / this.size);

        playerGridCol = Math.max(0, Math.min(enemyMap[0].length - 1, playerGridCol));
        playerGridRow = Math.max(0, Math.min(enemyMap.length - 1, playerGridRow));

        ArrayList<int[]> currentEnemyGridPositions = new ArrayList<>();
        for (int r = 0; r < enemyMap.length; r++) {
            for (int c = 0; c < enemyMap[r].length; c++) {
                if (enemyMap[r][c] == 8) { // BFS enemy type
                    currentEnemyGridPositions.add(new int[]{c, r});
                }
            }
        }

        if ((this.counter % 50) == 0) {
            for (int[] enemyPos : currentEnemyGridPositions) {
                int enemyCol = enemyPos[0];
                int enemyRow = enemyPos[1];

                if (enemyMap[enemyRow][enemyCol] != 8) { // Check if enemy is still there
                    continue;
                }

                int[] nextStep = findNextStepBFS(new int[]{enemyCol, enemyRow}, new int[]{playerGridCol, playerGridRow});

                if (nextStep != null) {
                    int nextCol = nextStep[0];
                    int nextRow = nextStep[1];

                    if ((nextCol != enemyCol || nextRow != enemyRow) &&
                            nextRow >= 0 && nextRow < enemyMap.length && nextCol >= 0 && nextCol < enemyMap[0].length) {

                        int targetCellContent = enemyMap[nextRow][nextCol];
                        if (targetCellContent != 0 && targetCellContent != 7 && targetCellContent != 8 && targetCellContent != 9 || (nextCol == playerGridCol && nextRow == playerGridRow)) {
                           enemyMap[enemyRow][enemyCol] = 99;
                           enemyMap[nextRow][nextCol] = 8;
                        }
                    }
                }
            }
        }

        ArrayList<Enemy> enemiesToReturn = new ArrayList<>();
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[r].length; c++) {
                if (map[r][c] == 8) {
                    enemiesToReturn.add(new Enemy((int) (c * size + this.x), (int) (r * size + this.y), size, size, 0, 0, 8));
                }
            }
        }

        Rectangle playerHitbox = new Rectangle(playerScreenX - size / 2, playerScreenY - size / 2, size, size);
        for (Enemy n : enemiesToReturn) {
            Rectangle enemyRect = new Rectangle(n.x, n.y, n.width, n.height);
            if (enemyRect.intersects(playerHitbox) && invinc == 0) {
                 if (playerMotion.hp > 0) playerMotion.hp--;
                Map.damage = true;
            }
        }

        if (Map.damage && invinc == 0) {

        }
        if (Map.damage) {
            invinc++;
            if (invinc >= 150) {
                invinc = 0;
                Map.damage = false;
            }
        }
        return enemiesToReturn;
    }


    ArrayList<Enemy> getEnemy() {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        int ex = 0, ey = 0;
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 7) {
                    if (enemies.size() < 0) {
                        enemies.add(new Enemy((int) (r * size + this.x), (int) (c * size + this.y), size, size, 0, 0, 1));
                    }

                    for (Enemy n : enemies) {
                        counter++;

                        //Tracks tile enemy replaces
                        n.prev = this.enemyMap[c + n.eY][r + n.eX];

                        //Enemy movement relative to player
                        if (n.x > 240) n.eX = -1;
                        if (n.x < 240) n.eX = 1;
                        if (n.y > 240) n.eY = -1;
                        else if (n.y < 240) n.eY = 1;

                        //Wall collision
                        if (this.enemyMap[c][r + 1] == 0) {n.eX = -1;}
                        if (this.enemyMap[c][r - 1] == 0) {n.eX = 1;}
                        if (this.enemyMap[c + 1][r] == 0) {n.eY = -1;}
                        if (this.enemyMap[c - 1][r] == 0) {n.eY = 1;}

                        //Removing and re-adding enemy
                        if ((counter % 50) == 0) {
                            if (this.enemyMap[c + n.eY][r + n.eX] != 0) {
                                this.enemyMap[c + n.eY][r + n.eX] = 7;
                                this.enemyMap[c][r] = n.prev;
                            }
                        }
                    }
                    enemies.add(new Enemy((int) (r * size + this.x), (int) (c * size + this.y), size, size, 0, 0, 7));
                }
            }
        }

        Rectangle playerHitbox = new Rectangle(240 - size / 2, 240 - size / 2, size, size);
        for (Enemy n : enemies) {
            Rectangle enemyRect = new Rectangle(n.x, n.y, n.width, n.height);
            if (enemyRect.intersects(playerHitbox) && invinc == 0) {
                if (playerMotion.hp > 0) playerMotion.hp--;
                Map.damage = true;
            }
        }
        if (Map.damage) {
            invinc++;
            if (invinc >= 150) {
                invinc = 0;
                Map.damage = false;
            }
        }
        return enemies;
    }


    void draw(Graphics g, int panel){
        counter++;
        if (panel == 1) {
            for (Rectangle wall : getWalls()) {
                g.setColor(Color.black);
                g.fillRect(wall.x, wall.y, size, size);
            }

            for (Rectangle wall : getCircuit()) {
                g.setColor(circuitColor);
                g.fillRect(wall.x, wall.y, size, size);
            }
            for (Gate gate : gates) {
                gate.draw(g, this.x, this.y, this.size);
            }
            for (Gate gate : gatesFake) {
                gate.draw(g, this.x, this.y, this.size);
            }
            for (Rectangle open : getOpen()) {
                g.setColor(Color.black);
                g.fillRect(open.x, open.y, size, size);
            }
            for(Rectangle n: getEnemy()) {
                g.setColor(Color.red);
                g.fillRect(n.x, n.y, n.width, n.height);
            }

            for (Enemy bfsEnemy : getBFSEnemy()) { // BFS enemies (type 8)
                g.setColor(Color.orange);
                g.fillRect(bfsEnemy.x, bfsEnemy.y, bfsEnemy.width, bfsEnemy.height);
            }

            for (Enemy smartEnemy : getSmartEnemy()) { // Smart enemies (type 9)
                g.setColor(Color.MAGENTA);
                g.fillRect(smartEnemy.x, smartEnemy.y, smartEnemy.width, smartEnemy.height);
            }

            for (Rectangle powerup : getSquares(2)) {
                g.setColor(Color.GREEN);
                g.fillRect(powerup.x, powerup.y, powerup.width, powerup.height);
            }
            for (Rectangle material : getMaterials()) {
                g.setColor(Color.YELLOW);
                g.fillRect(material.x, material.y, material.width, material.height);
            }
            for (Rectangle powerup : getSquares(enemyMap, 8)) {
                g.setColor(Color.pink);
                g.fillRect(powerup.x, powerup.y, powerup.width, powerup.height);
            }

        } else if (panel == 2) {
            // Panel 2 drawing logic
        } else if (panel == 3) {
            // Panel 3 drawing logic, e.g., game over screen
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("Game Over", 100, 200);
        }
    }

    boolean killEnemyAt(double bx, double by) {
        int gridX = (int) ((bx - this.x) / size);
        int gridY = (int) ((by - this.y) / size);

        if (gridY >= 0 && gridY < map.length && gridX >= 0 && gridX < map[0].length) {
            if (map[gridY][gridX] == 7 || map[gridY][gridX] == 8 || map[gridY][gridX] == 9) {
                map[gridY][gridX] = 1; // Turn into empty path
                return true;
            }
        }
        return false;
    }
    boolean checkInRect(Player p, Rectangle r) {
        if (p.getTop().intersects(r)) {
            return true;
        }
        if (p.getBottom().intersects(r)) {
            return true;
        }
        if (p.getLeft().intersects(r)) {
            return true;
        }
        if (p.getRight().intersects(r)) {
            return true;
        }
        return false;
    }
    boolean checkCollision(Player p, Rectangle r) {
        if (p.getTop().intersects(r)) {
            this.vy[1] = 0;
            this.y -= this.v;
            return true;
        }
        if (p.getBottom().intersects(r)) {
            this.vy[0] = 0;
            this.y += this.v;
            return true;
        }
        if (p.getLeft().intersects(r)) {
            this.vx[1] = -0;
            this.x -= this.v;
            return true;
        }
        if (p.getRight().intersects(r)) {
            this.vx[0] = 0;
            this.x += this.v;
            return true;
        }
        return false;
    }
    boolean gateUiClose(){
        /*
        for(Gate gate : gates) {
            if (gate.getSquare(this.x, this.y).x > 100 && gate.getSquare(this.x, this.y).x < 280 && gate.getSquare(this.x, this.y).y > 100 && gate.getSquare(this.x, this.y).y < 280) {
                return true;
            }
        }

        return false;
         */
        return this.GateCollision;
    }
    JPanel getGateUi() {
        for(Gate gate : gates) {
            //if (gate.getSquare(this.x, this.y).x > 100 && gate.getSquare(this.x, this.y).x < 280 && gate.getSquare(this.x, this.y).y > 100 && gate.getSquare(this.x, this.y).y < 280) {
            if (this.GateCollision){
                System.out.println("Gate UI found at current position.");
                return gate.getPanel();
            }
        }
        System.out.println("No gate UI found at current position.");
        //return gates.getFirst().getPanel();
        return new JPanel();
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == powerupTimer) {
            powerupcount++;
            if (powerupcount >= 100) { // Duration of power-up
                POWERUPSPEED = 1; // Reset speed
                powerupTimer.stop();
                powerupcount = 0;
            }
        }
    }
}