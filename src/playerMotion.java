import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class playerMotion extends JFrame
        implements KeyListener, MouseMotionListener, ActionListener{             // ← added MouseMotionListener
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;

    Map mainMap = new Map(-100, -100, 10, 1);
    Player player = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20, 0.3,
            new Color(0, 0, 0),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));

    /* track mouse for bullet direction */
    int mouseX = WIDTH / 2;
    int mouseY = HEIGHT / 2;

    public static void main(String[] args) { 
    	new playerMotion(); }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP)    mainMap.movedown();
        if (key == KeyEvent.VK_LEFT)  mainMap.moveright();
        if (key == KeyEvent.VK_DOWN)  mainMap.moveup();
        if (key == KeyEvent.VK_RIGHT) mainMap.moveleft();
        if (key == KeyEvent.VK_SPACE) player.shoot(mouseX, mouseY); // ← fire bullet
        if (key == KeyEvent.VK_P) {mainMap.payGate = true;}
        if (key == KeyEvent.VK_O) {mainMap.openGate = true;}
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP)    mainMap.vy[1] = 0;
        if (key == KeyEvent.VK_LEFT)  mainMap.vx[1] = 0;
        if (key == KeyEvent.VK_DOWN)  mainMap.vy[0] = 0;
        if (key == KeyEvent.VK_RIGHT) mainMap.vx[0] = 0;
        if (key == KeyEvent.VK_P) mainMap.payGate = false;
        if (key == KeyEvent.VK_O) mainMap.openGate = false;
    }

    public void keyTyped(KeyEvent e) {}

    playerMotion() {
        setTitle("HELLOW WORLD");
        setSize(500, 500);                    // frame stays 500×500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        DrawingPanel p = new DrawingPanel();
        panel.add(p);
        add(panel);
        // pack();                            // ← removed: keeps window from shrinking

        addKeyListener(this);
        addMouseMotionListener(this);         // ← track mouse
        setFocusable(true);
        setVisible(true);

        playAnimation();
    }

    public void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel() { setPreferredSize(new Dimension(500, 500)); }
        @Override
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
            player.update(WIDTH, HEIGHT);     // ← advance bullets
            Toolkit.getDefaultToolkit().sync();
            repaint();
            pause(5);
        }
    }

    /* MouseMotionListener methods */
    public void mouseMoved(MouseEvent e)  { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseDragged(MouseEvent e){ mouseMoved(e); }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
