import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;

public class playerMotion extends JFrame
        implements KeyListener, MouseMotionListener, ActionListener {

    public int WIDTH = 500, HEIGHT = 500;
    static final int bounds = 70;

    Map    mainMap = new Map(-100, -100, 10, 1);
    Player player  = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20, 0.3,
            new Color(0, 0, 0),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));

    /* track facing / shooting direction (-1/0/1) – default UP  */
    int dirX = 0, dirY = -1;

    /* not used for shooting any more but kept for mouse look  */
    int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;

    public static void main(String[] args) { new playerMotion(); }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP)    { mainMap.movedown();  dirX = 0;  dirY = -1; }
        if (key == KeyEvent.VK_LEFT)  { mainMap.moveright(); dirX = -1; dirY = 0; }
        if (key == KeyEvent.VK_DOWN)  { mainMap.moveup();    dirX = 0;  dirY = 1; }
        if (key == KeyEvent.VK_RIGHT) { mainMap.moveleft();  dirX = 1;  dirY = 0; }

        /* SPACE: shoot along last movement vector */
        if (key == KeyEvent.VK_SPACE) {
            double cx = player.x + player.width  / 2.0;
            double cy = player.y + player.height / 2.0;
            /* push aim point far enough so Bullet normalises direction */
            player.shoot(cx + dirX * 1000, cy + dirY * 1000);
        }

        if (key == KeyEvent.VK_P) mainMap.payGate  = true;
        if (key == KeyEvent.VK_O) mainMap.openGate = true;
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP)    mainMap.vy[1] = 0;
        if (key == KeyEvent.VK_LEFT)  mainMap.vx[1] = 0;
        if (key == KeyEvent.VK_DOWN)  mainMap.vy[0] = 0;
        if (key == KeyEvent.VK_RIGHT) mainMap.vx[0] = 0;
        if (key == KeyEvent.VK_P)     mainMap.payGate  = false;
        if (key == KeyEvent.VK_O)     mainMap.openGate = false;
    }

    public void keyTyped(KeyEvent e) {}

    playerMotion() {
        setTitle("HELLOW WORLD");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        DrawingPanel p = new DrawingPanel();
        panel.add(p);
        add(panel);

        addKeyListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        setVisible(true);

        playAnimation();
    }

    public void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel() { setPreferredSize(new Dimension(500, 500)); }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            mainMap.draw(g);
            player.draw(g);
            g.setColor(Color.BLACK);
            g.drawString("Materials Count: " + player.getInventory(), 10, 15);
        }
    }

    public void playAnimation() {
        while (true) {
            mainMap.move(player);
            player.update(WIDTH, HEIGHT);          // advance bullets

            /* quick bullet-vs-enemy check – remove bullet + enemy cell */
            Iterator<Player.Bullet> it = player.bullets.iterator();
            while (it.hasNext()) {
                Player.Bullet b = it.next();
                if (mainMap.killEnemyAt(b.getX(), b.getY())) it.remove();
            }

            Toolkit.getDefaultToolkit().sync();
            repaint();
            pause(5);
        }
    }

    /* MouseMotionListener */
    public void mouseMoved(MouseEvent e)  { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseDragged(MouseEvent e){ mouseMoved(e); }

    public void actionPerformed(ActionEvent e) { /* unused */ }
}