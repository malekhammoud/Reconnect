import java.awt.*;
import javax.swing.*;
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
    ArrayList<Gate> gates  = new ArrayList<Gate>();
	int counter = 1;
	int eX = 1, eY;
	int prev = 1;

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
    Map(double x, double y, int size, double v) {
        this.x = x;
        this.y = y;
        this.size= size;
        this.v = v;

        /*
        * 0 = empty
        * 1 = wall
        * 2 = gate
        * 3 = circuit
        * 7 = enemy
        * 10 = cookie
         */
        this.map = new int[][] {
               {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,10,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,1,1,1,1,1,1,0,0,0,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,0,1,1,1,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3,3,3,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,7,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,0,3,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,4,1,1,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,4,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,2,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,11,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
               {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
               {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};

            for (int c = 0; c < this.map.length; c++){
                for (int r = 0; r < this.map[c].length; r++){
                    if (this.map[c][r] == 4){
                        gates.add(new Gate(r, c, 10));
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
            for(Rectangle wall : getWalls()) {
                //if (player.getrect().intersects(wall)) {
                this.checkCollision(player, wall);
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
                   this.v += 5;
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
            boolean allOpen = true;
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


    ArrayList<Rectangle> getEnemy(){
        ArrayList<Rectangle> enemies = new ArrayList<Rectangle>();
        for (int c = 0; c < this.map.length; c++){
            for (int r = 0; r < this.map[c].length; r++){
                if (this.map[c][r] == 7){
                    enemies.add( new Rectangle((int) (r*size+this.x), (int) (c*size+this.y), size, size));
                    counter++;
                    //Wall collision
                    if(this.map[c][r + 1] == 0 || this.map[c + 1][r + 1] == 0) {eX = -1;}
                    if(this.map[c][r - 1] == 0){eX = 1;}
                    if(this.map[c + 1][r] == 0) {eY = -1;}
                    if(this.map[c - 1][r] == 0) {eY = 1;}

                    //Removing and re-adding enemy
                    if((counter % 30) == 0) {
                        this.map[c][r] = prev;
                        prev = this.map[c + eY][r + eX];
                    	this.map[c + eY][r + eX] = 7;
                    }
                    //Enemy movement relative to player
                    for(Rectangle n: enemies) {
                    	if(n.x > 240) eX = -1;
                    	if(n.x < 240) eX = 1;
                    	if(n.y > 240) eY = -1;
                    	if(n.y < 240) eY = 1;
                    }

                }
            }
        }
        return enemies;
    }

    void draw(Graphics g){
        for(Rectangle wall : getWalls()){
            g.setColor(Color.black);
            g.fillRect(wall.x,wall.y,size,size);
        }
        for(Rectangle material: getMaterials()){
            g.setColor(Color.green);
            g.fillRect(material.x,material.y,size,size);
        }
        for(Rectangle wall : getCircuit()){
            g.setColor(circuitColor);
            g.fillRect(wall.x,wall.y,size,size);
        }
        for(Gate gate: gates){
            gate.draw(g, this.x, this.y, this.size);
        }
        for(Rectangle open: getOpen()){
            g.setColor(Color.black);
            g.fillRect(open.x,open.y,size,size);
        }
        for(Rectangle enemies: getEnemy()) {
        	g.setColor(Color.red);
        	g.fillRect(enemies.x, enemies.y, size, size);
        }
        for(Rectangle powerup: getSquares(10)){
            g.setColor(Color.yellow);
            g.fillRect(powerup.x,powerup.y, powerup.width,powerup.height);
        }
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
        if (p.getLeft().intersects(r)){
            this.x -= this.v;
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
                this.v -= 5;
                powerupTimer.stop();
                this.powerupcount = 0;
            }
        }
    }
}