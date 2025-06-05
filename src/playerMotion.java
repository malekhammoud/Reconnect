import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class playerMotion extends JFrame implements KeyListener, MouseMotionListener, ActionListener{             // ← added MouseMotionListener
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;
    CardLayout card;
    JPanel menus, title;
    String currentMenu;
    Timer timer;
    static int TIMESPEED = 10;
    double timeSec;
    int timeMin;
    static int hp = 3, setMain = 0;

    Map mainMap = new Map(-40, -40, 25, 0.1);
    Map menuMap = new Map(128, 128, 4, 0.1);
    Player player = new Player(525, 393, 2, 2, 0.3,
            new Color(0, 0, 0),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));
    Player Ghost = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
            new Color(253, 212, 6),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));

    /* track facing / shooting direction (-1/0/1) – default UP  */
    int dirX = 0, dirY = -1;

    /* not used for shooting any more but kept for mouse look  */
    int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;

    public static void main(String[] args) { new playerMotion(); }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (currentMenu.equals("MainGame")){
        if (key == KeyEvent.VK_UP) {mainMap.movedown();menuMap.movedown(); dirX = 0;  dirY = -1; }
        if (key == KeyEvent.VK_LEFT) {mainMap.moveright();menuMap.moveright();dirX = -1; dirY = 0;}
        if (key == KeyEvent.VK_DOWN) {mainMap.moveup();menuMap.moveup();dirX = 0;  dirY = 1; }
        if (key == KeyEvent.VK_RIGHT) {mainMap.moveleft();menuMap.moveleft();dirX = 1;  dirY = 0;}
        /* SPACE: shoot along last movement vector */
        if (key == KeyEvent.VK_SPACE) {
            double cx = player.x + player.width  / 2.0;
            double cy = player.y + player.height / 2.0;
            /* push aim point far enough so Bullet normalises direction */
            player.shoot(cx + dirX * 1000, cy + dirY * 1000);
        }
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
        if (key == KeyEvent.VK_UP) {
            mainMap.vy[1] = 0;
            menuMap.vy[1] = 0;
        }
        if (key == KeyEvent.VK_LEFT) {
            mainMap.vx[1] = 0;
            menuMap.vx[1] = 0;
        }
        if (key == KeyEvent.VK_DOWN) {
            mainMap.vy[0] = 0;
            menuMap.vy[0] = 0;
        }
        if (key == KeyEvent.VK_RIGHT) {
            mainMap.vx[0] = 0;
            menuMap.vx[0] = 0;
        }
        if (key == KeyEvent.VK_U) mainMap.payGate = false;
        if (key == KeyEvent.VK_I) mainMap.openGate = false;
        if (key == KeyEvent.VK_U && setMain == 0) setMain = 1;
        if (key == KeyEvent.VK_I && setMain == 0) setMain = 2;
    }

        @Override
        public void keyTyped(KeyEvent e) {}

    playerMotion() {
        setTitle("Reconnect");
        setSize(500, 500);                    // frame stays 500×500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(this.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        card = new CardLayout();
        menus = new JPanel(card);
        JPanel panel = new JPanel();
        JPanel mapMenu = new JPanel();
        JPanel inventoryMenu = new JPanel();
        JPanel title = new JPanel();

        panel.setBackground(Color.BLACK);


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
        addMouseMotionListener(this);
        setFocusable(true);
        setVisible(true);
        Timer timer = new Timer(TIMESPEED, this);
        timer.start();

        playAnimation();
    }

    public void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private class DrawingPanel extends JPanel {
        int panel;
        Font font = new Font("Serif", Font.BOLD, 24);
        DrawingPanel(int pan) { setPreferredSize(new Dimension(1050, 785)); panel = pan;}
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //Graphics2D g2 = (Graphics2D)g;
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Title screen
           if(setMain == 0) {
               g.setColor(Color.WHITE);
               g.fillRect(0, 0, 1050, 785);
               g.setColor(Color.BLACK);
               g.drawString("Reconnect :)", 100, 100);
               g.drawString("Press U(a) to continue", 100, 120);
               g.drawString("Press I(b) to see highscores", 100, 140);
           }

           //Display highscores
           if(setMain == 2) {

           }

            if (panel == 1 && setMain == 1){
                mainMap.draw(g, 1);
                player.draw(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 410, 1050);
                g.fillRect(705, 0, 670, 1050);
                g.fillRect(0, 560, 1050, 500);
                g.fillRect(0, 0, 1050, 280);
                g.setColor(Color.WHITE);
                g.drawString("Materials Count: " + player.getInventory(), 10, 15);
                //Displays time
                String timeSecString = String.format("%.2f", timeSec);
                g.drawString("Time :" + timeMin + ":" + timeSecString, 10, 35);

                //Display coloured rectangles dependent on amount of hp
                if(hp == 3) {g.setColor(Color.GREEN);}
                if(hp == 2) {g.setColor(Color.YELLOW);}
                if(hp == 1) {g.setColor(Color.RED);}
                if(hp <= 0) {
                    //Gameover
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, 1050, 785);
                    g.setColor(Color.BLACK);
                    g.drawString("Game Over :(", 30, 35);
                    g.setColor(Color.RED);
                }
                g.fillRect(30, 650, 80, 100);
                if(mainMap.allOpen){
                    g.setColor(Color.GREEN);
                    g.fillRect(0, 0, 1050, 785);
                    g.setColor(Color.BLACK);
                    g.drawString("You Win :)", 30, 35);
                    g.setColor(Color.BLACK);
                    g.drawString("Time :" + timeMin + ":" + timeSecString, 10, 60);


                }
            }
            //Display material count
            if (panel == 2 && setMain == 1){
                //player.draw(g);
                g.setColor(Color.BLACK);
                g.setFont(font);
                g.drawString("Materials Count: " + player.getInventory(), 220, 238);
            }

            //Display minimap
            if (panel == 3 && setMain == 1){
                menuMap.draw(g,1);
                Ghost.draw(g);
            }
        }
    }

    public void playAnimation() {
        while (true) {
            mainMap.move(player);
            menuMap.move(Ghost);
            if (currentMenu.equals("MainGame")){
                player.update(WIDTH, HEIGHT, mainMap.size);     // ← advance bullets and update player size to map
                /* quick bullet-vs-enemy check – remove bullet + enemy cell */
                Iterator<Player.Bullet> it = player.bullets.iterator();
                while (it.hasNext()) {
                    Player.Bullet b = it.next();
                    if (mainMap.killEnemyAt(b.getX(), b.getY())) it.remove();
                }
            }

            if (currentMenu.equals("Map"))Ghost.update(WIDTH, HEIGHT, menuMap.size);
            Toolkit.getDefaultToolkit().sync();
            repaint();
            pause(5);
        }
    }

    /* MouseMotionListener */
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
        //Timer goes until all gates open
        if(!mainMap.allOpen) {
            if (timeSec >= 60) {
                timeMin++;
                timeSec = 0;
            }
            timeSec += 0.02;
        }
    }}
