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
        bullets.add(new Bullet(cx, cy, mx - cx, my - cy, Color.ORANGE));
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
    }

    void drawSingle(Graphics g) {
        g.setColor(c);
        g.fillRect((int) x, (int) y, width, height);
    }

    void draw(Graphics g) {
        for (Bullet b : bullets) b.draw((Graphics2D) g);
        for (Player sh : shadows) sh.drawSingle(g);
        g.setColor(c);
        g.fillRect((int) x, (int) y, width, height);
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
        private double x, y;
        private final double vx, vy;
        private static final int    R = 6;
        private static final double SPEED = 8;
        private final Color color;

        Bullet(double sx, double sy, double dx, double dy, Color c) {
            double len = Math.hypot(dx, dy);
            vx = (dx / len) * SPEED;
            vy = (dy / len) * SPEED;
            x  = sx;
            y  = sy;
            color = c;
        }

        void update()                 { x += vx; y += vy; }
        boolean offScreen(int w,int h){ return x < -R || x > w+R || y < -R || y > h+R; }
        void draw(Graphics2D g)       { g.setColor(color); g.fill(new Ellipse2D.Double(x-R, y-R, R*2, R*2)); }

        /* getters for collision */
        double getX() { return x; }
        double getY() { return y; }
    }
}
