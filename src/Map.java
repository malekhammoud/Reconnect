import java.awt.*;
import javax.swing.*;
import java.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Map implements ActionListener{
    double v;
    double[] vy = {0,0};
    double v_normal;
    double v_slow;
    double[] vx = {0,0};
    int width, height;
    double  x, y;
    int size;
    int Menusize;
    ArrayList<Gate> gates  = new ArrayList<Gate>();
    ArrayList<Gate> gatesFake  = new ArrayList<Gate>();
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
    boolean payGate = false;
    boolean openGate= false;
    boolean moving = false;
    Color circuitColor = Color.BLUE;
    Map(double x, double y, int size, double v, int[][] map) {
        this.x = x;
        this.y = y;
        this.size= size;
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
         * 0 = empty
         * 1 = wall
         * 2 = gate
         * 3 = circuit
         * 7 = enemy
         * 10 = cookie
         */
        this.map = this.read_map_from_file("" + mapLevel);

        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 4) {
                    gates.add(new Gate(r, c, this.size));
                }
                else if (this.map[c][r] == 5) {
                    gatesFake.add(new Gate(r, c, this.size));
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
         * 1 = empty
         * 2 = gate
         * 5 = "dumb" gate
         * 3 = circuit
         * 7 = enemy
         * 10 = cookie
         */
        this.map = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 10, 1, 1, 1, 1, 10, 1, 1, 1, 1, 1, 10, 1, 1, 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 7, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
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
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 3, 3, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 2, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 3, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 5, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 1, 1, 1, 1, 1, 3, 1, 5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0,0, 0},
                {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 4) {
                    gates.add(new Gate(r, c, this.size));
                }
                else if (this.map[c][r] == 5) {
                    gatesFake.add(new Gate(r, c, this.size));
                }
            }
        }
    }
    private static int[] convert_to_int(String[] arr){
        int[] newarr = new int[arr.length];
        for (int i = 0; i < arr.length; i++){
            newarr[i] = Integer.parseInt(arr[i]);
        }
        return newarr;
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

    void mapScrub(){
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 2)this.map[c][r] = 1;
                if (this.map[c][r] == 7)this.map[c][r] = 1;
                //if (this.map[c][r] == 3)this.map[c][r] = 1;
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

            for(Gate gate: gatesFake) {
                //if (player.getrect().intersects(wall)) {
                if(this.checkCollision(player, gate.getrect(this.x, this.y, this.size))){
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
                //if (player.getrect().intersects(wall)) {
                if(this.checkCollision(player, gate.getrect(this.x, this.y, this.size))){
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

    /**
     * LOTS OF REPITITION BELLOW, NEED TO FIX:
     */
    ArrayList<Rectangle> getOpen(){
        ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 1){
                    walls.add( new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size));
                }
            }
        }
        return walls;
    }
    ArrayList<Rectangle> getWalls(){
        ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 0){
                    walls.add( new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size));
                }
            }
        }
        return walls;
    }
    ArrayList<Rectangle> getCircuit(){
        ArrayList<Rectangle> wall= new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 3){
                    wall.add(new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), 10, 10));
                }
            }
        }
        return wall;
    }

    ArrayList<Rectangle> getMaterials(){
        ArrayList<Rectangle> walls = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 2){
                    walls.add( new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size));
                }
            }
        }
        return walls;
    }

    //this is how to do it:
    ArrayList<Square> getSquares(int code){
        ArrayList<Square> squares= new ArrayList<Square>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == code){
                    squares.add(new Square((int) (r*size+this.x), (int) (c*size+this.y), size, size, r, c));
                }
            }
        }
        return squares;
    }

    void updateGate(){
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 4){
                    gates.add( new Gate((int) (r*size+this.x), (int) (c*size+this.y), size));
                }
            }
        }
    }

    int[] bfs(int[] start, int[] end)  {
        boolean[][] visited = new boolean[map.length][map[0].length];
        Queue queue = new Queue(10000);

        queue.enqueue(start);
        visited[start[1]][start[0]] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.dequeue();
            int x = current[0];
            int y = current[1];

            if (x == end[0] && y == end[1]) {
                System.out.println("Path found to the end of the map.");
                return current;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (Math.abs(dx) != Math.abs(dy)) { // Only allow horizontal and vertical moves
                        int newX = x + dx;
                        int newY = y + dy;

                        if (newX >= 0 && newX < map[0].length && newY >= 0 && newY < map.length &&
                                !visited[newY][newX] && map[newY][newX] != 0) {
                            visited[newY][newX] = true;
                            queue.enqueue(new int[]{newX, newY});
                        }
                    }
                }
            }
        }
        System.out.println("No path found to the end of the map.");
        return null;
    }

    ArrayList<Enemy> getSmartEnemy() {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 7) {

                    // if (enemies.size() < 2) {
                    enemies.add(new Enemy((int) (r * size + this.x), (int) (c * size + this.y), size, size, 0, 0, 1));
                    // }

                    for (Enemy n : enemies) {
                        counter++;

                        //Tracks tile enemy replaces
                        n.prev = this.map[c + n.eY][r + n.eX];

                        //Enemy movement relative to player
                        if (n.x > 240) n.eX = -1;
                        if (n.x < 240) n.eX = 1;
                        if (n.y > 240) n.eY = -1;
                        else if (n.y < 240) n.eY = 1;

                        //Wall collision
                        if (this.map[c][r + 1] == 0) {n.eX = -1;}
                        if (this.map[c][r - 1] == 0) {n.eX = 1;}
                        if (this.map[c + 1][r] == 0) {n.eY = -1;}
                        if (this.map[c - 1][r] == 0) {n.eY = 1;}

                        //Removing and re-adding enemy
                        if ((counter % 50) == 0) {
                            if (this.map[c + n.eY][r + n.eX] != 0) {
                                this.map[c + n.eY][r + n.eX] = 7;
                                this.map[c][r] = n.prev;
                            }
                        }

                    }
                }
            }
        }
        for (Enemy n: enemies) {
            //Lose hp if enemy is where player is
            if (Player.intersect(enemies) && n.x >= 230 && n.x <= 265 && n.y >= 230 && n.y <= 265 && invinc == 0) {
                playerMotion.hp--;
                damage = true;
            }
            //Invincibility frames
            if(damage) {
                invinc++;
                if(invinc == 150) {
                    invinc = 0;
                    damage = false;
                }
            }
        }
        return enemies;
    }
    ArrayList<Enemy> getEnemy() {
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        for (int c = 0; c < this.map.length; c++) {
            for (int r = 0; r < this.map[c].length; r++) {
                if (this.map[c][r] == 7) {
                   // if (enemies.size() < 2) {
                        enemies.add(new Enemy((int) (r * size + this.x), (int) (c * size + this.y), size, size, 0, 0, 1));
                   // }

                    for (Enemy n : enemies) {
                        counter++;

                        //Tracks tile enemy replaces
                        n.prev = this.map[c + n.eY][r + n.eX];

                        //Enemy movement relative to player
                        if (n.x > 240) n.eX = -1;
                        if (n.x < 240) n.eX = 1;
                        if (n.y > 240) n.eY = -1;
                        else if (n.y < 240) n.eY = 1;

                        //Wall collision
                        if (this.map[c][r + 1] == 0) {n.eX = -1;}
                        if (this.map[c][r - 1] == 0) {n.eX = 1;}
                        if (this.map[c + 1][r] == 0) {n.eY = -1;}
                        if (this.map[c - 1][r] == 0) {n.eY = 1;}

                        //Removing and re-adding enemy
                        if ((counter % 50) == 0) {
                            if (this.map[c + n.eY][r + n.eX] != 0) {
                                this.map[c + n.eY][r + n.eX] = 7;
                                this.map[c][r] = n.prev;
                            }
                        }

                    }
                }
            }
        }
        for (Enemy n: enemies) {
            //Lose hp if enemy is where player is
            if (Player.intersect(enemies) && n.x >= 230 && n.x <= 265 && n.y >= 230 && n.y <= 265 && invinc == 0) {
                playerMotion.hp--;
                damage = true;
            }
            //Invincibility frames
            if(damage) {
                invinc++;
                if(invinc == 150) {
                    invinc = 0;
                    damage = false;
                }
            }
        }
            return enemies;
    }


    void draw(Graphics g, int panel){
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
                g.fillRect(n.x, n.y, size, size);
            }
            for(Rectangle powerup: getSquares(2)){
                g.setColor(Color.green);
                g.setColor(Color.yellow);
                g.fillRect(powerup.x,powerup.y, powerup.width,powerup.height);
            }
            for(Rectangle powerup: getSquares(10)){
                g.setColor(Color.yellow);
                g.fillRect(powerup.x,powerup.y, powerup.width,powerup.height);
            }
            for(Rectangle powerup: getSquares(6)){
                g.setColor(Color.pink);
                g.fillRect(powerup.x,powerup.y, powerup.width,powerup.height);
            }
        }
        /*
        if (panel == 3) {
                for (Rectangle wall : getWalls()) {
                    g.setColor(Color.black);
                    g.fillRect(wall.x, wall.y, Menusize, Menusize);
                }
                for (Rectangle material : getMaterials()) {
                    g.setColor(Color.green);
                    g.fillRect(material.x, material.y, Menusize, Menusize);
                }
                for (Rectangle wall : getCircuit()) {
                    g.setColor(circuitColor);
                    g.fillRect(wall.x, wall.y, Menusize, Menusize);
                }
                for (Gate gate : gates) {
                    gate.draw(g, this.x, this.y, this.Menusize);
                }
                for (Rectangle open : getOpen()) {
                    g.setColor(Color.black);
                    g.fillRect(open.x, open.y, Menusize, Menusize);
                }
                for (Rectangle enemies : getEnemy()) {
                    g.setColor(Color.red);
                    g.fillRect(enemies.x, enemies.y, Menusize, Menusize);
                }
        } */
    }

    boolean killEnemyAt(double bx, double by) {
        int col = (int) ((bx - this.x) / size);
        int row = (int) ((by - this.y) / size);

        if (row >= 0 && row < map.length && col >= 0 && col < map[row].length) {
            if (map[row][col] == 7) {           // 7 == enemy
                map[row][col] = 10;              // 1 == walkable/open
                return true;                    // bullet hit
            }
        }
        return false;                           // nothing happened
    }

    boolean checkCollision(Player p, Rectangle r){
        if (p.getTop().intersects(r)) {
            this.vy[1]=0;
            this.y -= this.v;
            return true;
        }
        if (p.getBottom().intersects(r)) {
            this.vy[0]=0;
            this.y += this.v;
            return true;
        }
        if (p.getLeft().intersects(r)) {
            this.vx[1]=-0;
            this.x -= this.v;
            return true;
        }
        if (p.getRight().intersects(r)) {
            this.vx[0]=0;
            this.x += this.v;
            return true;
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == powerupTimer) {
            this.powerupcount+=1;
            if(this.powerupcount >= 10000){
                this.v -= 3;
                this.gothoughwalls = false;
                invinc = 0;
                powerupTimer.stop();
                this.powerupcount = 0;

            }

        }
    }
}