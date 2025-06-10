import java.awt.*;
import java.util.ArrayList;

public class Enemy extends Rectangle {
    int eX, eY;  // Direction
    int gridX, gridY;  // Grid position
    int type;

    // Your existing constructor plus:
    Enemy(int x, int y, int width, int height, int eX, int eY, int type) {
        super(x, y, width, height);
        this.eX = eX;
        this.eY = eY;
        this.type = type;
    }
}

