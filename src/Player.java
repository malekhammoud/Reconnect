import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Rectangle {
    double v;
    int size;
    int width, height;
    public double x, y;                  // keep public so caller can read centre
    int inventory;
    int hp;
    List<Player> shadows = new ArrayList<>();
    List<Bullet> bullets = new ArrayList<>();
    Color c;
    Rectangle centerBoundary, centerBoundarySm;
    // sprite manager
    SpriteManager spriteManager;

    public Player(double x, double y, int w, int h, double v, Color c) {
        super((int) x, (int) y, w, h);
        this.x = x;
        this.y = y;
        this.v = v;
        this.width = w;
        this.height = h;
        this.size = w;
        this.c = c;
        this.centerBoundary = new Rectangle();
        this.centerBoundarySm = new Rectangle();
        // init sprite manager
        this.spriteManager = new SpriteManager();
    }

    public Player(double x, double y, int w, int h, double v,
                  Color c, Rectangle centerBoundary) {
        this(x, y, w, h, v, c);
        this.centerBoundary = centerBoundary;
        this.centerBoundarySm = new Rectangle(
                centerBoundary.x + centerBoundary.width  / 4,
                centerBoundary.y + centerBoundary.height / 4,
                centerBoundary.width  / 2,
                centerBoundary.height / 2);
    }

    public Player(int x, int y, int w, int h, double v) {
        super(x, y, w, h);
        this.x = x;
        this.y = y;
        this.v = v;
        this.width = w;
        this.height = h;
        this.c = new Color(0f, 0f, 0f, .03f);
        this.centerBoundary = new Rectangle();
        this.centerBoundarySm = new Rectangle();
    }

    public static boolean intersect(ArrayList<Enemy> enemies) {
        return true;
    }

    void addInventory()       { inventory++; }
    void removeInventory()    { inventory--; }
    int  getInventory()       { return inventory; }

    /* shoot toward an arbitrary screen point */
    void shoot(double mx, double my) {
        double cx = x + width / 2.0, cy = y + height / 2.0;
        bullets.add(new Bullet(cx, cy, mx, my));
    }

    /* shoot using keyboard direction (-1, 0, 1) */
    void shoot(int dirX, int dirY) {
        double cx = x + width / 2.0, cy = y + height / 2.0;
        // Scale the direction to be more visible
        double scaleFactor = 20.0;
        double targetX = cx + (dirX * scaleFactor);
        double targetY = cy + (dirY * scaleFactor);
        bullets.add(new Bullet(cx, cy, targetX - cx, targetY - cy));
        System.out.println("Shooting from (" + cx + "," + cy + ") toward direction (" + dirX + "," + dirY + ")");
    }

    void update(int w, int h, int mapScale) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update();
            if (b.offScreen(w, h)) it.remove();
        }
        setLocation((int) x, (int) y);
        // may be removable but will help if we ever resize the main map or minimap on the fly
        this.width = size * mapScale;
        this.height = size * mapScale;
        // update sprite animation
        spriteManager.updateAnimation();
    }

    void drawSingle(Graphics g) {
        // use sprite if we have one, otherwise fallback to rectangle
        if (spriteManager != null) {
            spriteManager.drawSprite(g, (int) x, (int) y, width, height);
        } else {
            g.setColor(c);
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    void draw(Graphics g) {
        for (Bullet b : bullets) b.draw(g);
        for (Player sh : shadows) sh.drawSingle(g);

        // use sprite for main player
        if (spriteManager != null) {
            spriteManager.drawSprite(g, (int) x, (int) y, width, height);
        } else {
            g.setColor(c);
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    Rectangle getTop()    { return new Rectangle((int) x + width/5,         (int) y,              width - width/5*2, height/2); }
    Rectangle getBottom() { return new Rectangle((int) x + width/5,         (int) y + height/2,   width - width/5*2, height/2); }
    Rectangle getRight()  { return new Rectangle((int) x + width/2,         (int) y + height/5,   width/2,           height - height/5*2); }
    Rectangle getLeft()   { return new Rectangle((int) x,                   (int) y + height/5,   width/2,           height - height/5*2); }
    Rectangle getrect()   { return new Rectangle((int) x,                   (int) y,              width,             height); }
    Rectangle getView()   {
        int vs = 4*this.width;
        return new Rectangle((int) x -vs/2,(int) y-vs/2,width+vs,height+vs);
    }

    /* very small inner class – just added simple getters */
    static class Bullet {
        double x, y;
        private double vx, vy;
        private static final int R = 40; // Much larger radius for better visibility
        private static final double SPEED = 3.0; // Even slower so bullets stay on screen longer
        private final Color color;

        Bullet(double sx, double sy, double dx, double dy) {
            this.x = sx;
            this.y = sy;
            color = Color.YELLOW; // Use a more visible color that stands out against the game

            // Calculate velocity vector with normalized direction
            double len = Math.sqrt(dx*dx + dy*dy);
            if (len < 0.001) { // Prevent division by zero
                // Default direction if input direction is too small
                vx = 0;
                vy = -SPEED; // Default to up
            } else {
                // Scale the direction by SPEED
                vx = (dx / len) * SPEED;
                vy = (dy / len) * SPEED;
            }
            System.out.println("Created bullet at (" + x + "," + y + ") with velocity (" + vx + "," + vy + ")");
        }

        void update() {
            x += vx;
            y += vy;
        }

        void draw(Graphics g) {
            // Save the current color to restore it later
            Color originalColor = g.getColor();


            // Main bullet (bright color)
            g.setColor(color);
            g.fillOval((int)(x - R+ 10), (int)(y - R +10), R, R);

            // Restore the original color
            g.setColor(originalColor);
        }

        boolean offScreen(int w, int h) {
            // More forgiving off-screen check to ensure bullets don't disappear too quickly
            return x < -R*4 || x > w+R*4 || y < -R*4 || y > h+R*4;
        }

        double getX() { return x; }
        double getY() { return y; }
    }
}