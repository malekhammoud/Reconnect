import java.awt.*;
import java.util.ArrayList;

public class Enemy extends Rectangle{
    int eX;
    int eY;
    int prev;
    int size;


    public Enemy(int x, int y, int width, int length, int height, int eX, int eY) {
        this.eX = eX;
        this.eY = eY;
        this.prev = prev;
        this.x = x;
        this.y = y;
        this.size = length;
    }
}

