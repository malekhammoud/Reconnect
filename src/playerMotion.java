import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class playerMotion extends JFrame
        implements KeyListener, MouseMotionListener, ActionListener{             // ← added MouseMotionListener
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;
    CardLayout card;
    JPanel menus;
    String currentMenu;

    Map mainMap = new Map(-40, -40, 10, 0.1);
    Map menuMap = new Map(128, 128, 4, 0.1);
    Player player = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
            new Color(0, 0, 0),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));
    Player Ghost = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
            new Color(253, 212, 6),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));

    /* track mouse for bullet direction */
    int mouseX = WIDTH / 2;
    int mouseY = HEIGHT / 2;

    public static void main(String[] args) { 
    	new playerMotion(); }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (currentMenu.equals("MainGame")){
        if (key == KeyEvent.VK_UP) {mainMap.movedown();menuMap.movedown();}
        if (key == KeyEvent.VK_LEFT) {mainMap.moveright();menuMap.moveright();}
        if (key == KeyEvent.VK_DOWN) {mainMap.moveup();menuMap.moveup();}
        if (key == KeyEvent.VK_RIGHT) {mainMap.moveleft();menuMap.moveleft();}

        if (key == KeyEvent.VK_SPACE) player.shoot(mouseX, mouseY); // ← fire bullet
        if (key == KeyEvent.VK_P) {
            mainMap.payGate = true;
            menuMap.payGate = true;
        }
        if (key == KeyEvent.VK_O) {
            mainMap.openGate = true;
            menuMap.openGate = true;
        }
        if (key == KeyEvent.VK_N) {
            SwapMenuTo("Map");
        }
        if (key == KeyEvent.VK_M) {
            SwapMenuTo("MainGame");
        }
        if (key == KeyEvent.VK_B) {
            SwapMenuTo("Inventory");
        }
            if (key == KeyEvent.VK_Z) {
                mainMap.Menusize += 2;
            }
            if (key == KeyEvent.VK_X) {
                mainMap.Menusize -= 2;
            }
        }
        if (currentMenu.equals("Inventory")){
            if (key == KeyEvent.VK_N) {
                SwapMenuTo("Map");
            }
            if (key == KeyEvent.VK_M) {
                SwapMenuTo("MainGame");
            }
            if (key == KeyEvent.VK_B) {
                SwapMenuTo("Inventory");
            }
        }
        if (currentMenu.equals("Map")){
            if (key == KeyEvent.VK_N) {
                SwapMenuTo("Map");
            }
            if (key == KeyEvent.VK_M) {
                SwapMenuTo("MainGame");
            }
            if (key == KeyEvent.VK_B) {
                SwapMenuTo("Inventory");
            }
            if (key == KeyEvent.VK_Z) {
                SwapMenuTo("Inventory");
            }
            if (key == KeyEvent.VK_X) {
                SwapMenuTo("Inventory");
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP)    {mainMap.vy[1] = 0;menuMap.vy[1] = 0;}
        if (key == KeyEvent.VK_LEFT)  {mainMap.vx[1] = 0;menuMap.vx[1] = 0;}
        if (key == KeyEvent.VK_DOWN)  {mainMap.vy[0] = 0;menuMap.vy[0] = 0;}
        if (key == KeyEvent.VK_RIGHT) {mainMap.vx[0] = 0;menuMap.vx[0] = 0;}
        if (key == KeyEvent.VK_P) mainMap.payGate = false;
        if (key == KeyEvent.VK_O) mainMap.openGate = false;
    }

    public void keyTyped(KeyEvent e) {}

    playerMotion() {
        setTitle("HELLOW WORLD");
        setSize(500, 500);                    // frame stays 500×500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        card = new CardLayout();
        menus = new JPanel(card);
        JPanel panel = new JPanel();
        JPanel mapMenu = new JPanel();
        JPanel inventoryMenu = new JPanel();

        menus.add(panel,"MainGame");
        menus.add(mapMenu,"Map");
        menus.add(inventoryMenu,"Inventory");
        DrawingPanel Drawing_p = new DrawingPanel(1);
        DrawingPanel Drawing_q = new DrawingPanel(2);
        DrawingPanel Drawing_b = new DrawingPanel(3);

        menuMap.mapScrub();

        panel.add(Drawing_p);
        inventoryMenu.add(Drawing_q);
        mapMenu.add(Drawing_b);
        SwapMenuTo("MainGame");

        add(menus);
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
        int panel;
        Font font = new Font("Serif", Font.BOLD, 24);
        DrawingPanel(int pan) { setPreferredSize(new Dimension(500, 500)); panel = pan;}
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (panel == 1){
                mainMap.draw(g,1);
                player.draw(g);
                g.setColor(Color.BLACK);
                g.drawString("Materials Count: " + player.getInventory(), 10, 15);
            }
            if (panel == 2){
                //player.draw(g);
                g.setColor(Color.BLACK);
                g.setFont(font);
                g.drawString("Materials Count: " + player.getInventory(), 220, 238);
            }
            if (panel == 3){
                menuMap.draw(g,1);
                Ghost.draw(g);
            }
        }
    }

    public void playAnimation() {
        while (true) {
            mainMap.move(player);
            menuMap.move(Ghost);
            if (currentMenu.equals("MainGame"))player.update(WIDTH, HEIGHT, mainMap.size);     // ← advance bullets and update player size to map

            if (currentMenu.equals("Map"))Ghost.update(WIDTH, HEIGHT, menuMap.size);
            Toolkit.getDefaultToolkit().sync();
            repaint();
            pause(5);
        }
    }

    public void SwapMenuTo(String menu){
        card.show(menus,menu);
        currentMenu = menu;
        System.out.println("swapping Menu to " + menu);
    }

    /* MouseMotionListener methods */
    public void mouseMoved(MouseEvent e)  { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseDragged(MouseEvent e){ mouseMoved(e); }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
