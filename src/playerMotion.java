import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.io.*;
import java.util.Scanner;

public class playerMotion extends JFrame implements KeyListener, MouseMotionListener, ActionListener{             // ← added MouseMotionListener
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;
    static CardLayout card = new CardLayout();
    JPanel menus;
    JPanel GateUi;
    String currentMenu;
    Timer timer;
    static int TIMESPEED = 10;
    double timeSec;
    int timeMin;
    static int hp = 3;

    JLayeredPane layeredPane; // Added JLayeredPane

    static Map mainMap = new Map(-40, -40, 10, 0.1, card);
    Map menuMap = new Map(128, 128, 4, 0.1, card);
    Player player = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
            new Color(0, 0, 0),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));
    Player Ghost = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
            new Color(253, 212, 6),
            new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));

    /* track facing / shooting direction (-1/0/1) – default UP  */
    int dirX = 0, dirY = -1;

    /* not used for shooting any more but kept for mouse look  */
    int mouseX = WIDTH / 2, mouseY = HEIGHT / 2;

    public static void main(String[] args) {
        new playerMotion(); }

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

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        menus = new JPanel(card);

        JPanel panel = new JPanel();
        JPanel mapMenu = new JPanel();
        JPanel inventoryMenu = new JPanel();
        GateUi = mainMap.getGateUi();

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

        menus.setBounds(0, 0, WIDTH, HEIGHT);
        layeredPane.add(menus, JLayeredPane.DEFAULT_LAYER);

        GateUi.setBounds(50, 50, 100, 100);
        GateUi.setVisible(true);
        layeredPane.add(GateUi, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);

        SwapMenuTo("MainGame");

        addKeyListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        this.timer = new Timer(TIMESPEED, this); // Initialize the class member timer
        this.timer.start();
        setVisible(true); // Call setVisible at the end

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
            //Graphics2D g2 = (Graphics2D)g;
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (panel == 1){
                mainMap.draw(g, 1);
                player.draw(g);
                g.setColor(Color.WHITE);
                g.fillRect(10, 4, 110, 35);
                g.setColor(Color.BLACK);
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
                    g.fillRect(0, 0, 500, 500);
                    g.setColor(Color.BLACK);
                    g.drawString("Game Over :(", 30, 35);
                    g.setColor(Color.RED);
                }
                g.fillRect(15, 400, 40, 50);
                if(mainMap.allOpen){
                    g.setColor(Color.GREEN);
                    g.fillRect(0, 0, 500, 500);
                    g.setColor(Color.BLACK);
                    g.drawString("You Win :)", 30, 35);
                    g.setColor(Color.BLACK);
                    g.drawString("Time :" + timeMin + ":" + timeSecString, 10, 60);
                            File scoreFile = new File("src/scores.txt");
                            FileReader in;
                            BufferedReader readFile;

                            String line;
                            String topScorer = "";
                            float highScore = 0;

                            try {
                                in = new FileReader(scoreFile);
                                readFile = new BufferedReader(in);

                                while ((line = readFile.readLine()) != null) {
                                    String[] parts = line.split(" ");
                                    String name = parts[0];
                                    float score = Float.parseFloat(parts[1]);

                                    if (score > highScore) {
                                        highScore = score;
                                        topScorer = name;
                                    }
                                }

                                readFile.close();
                                in.close();

                                if (!topScorer.isEmpty()) {
                                    g.drawString("High score: " + topScorer + " with " + highScore, 10, 80);
                                } else {
                                    System.out.println("No scores found.");
                                }

                            } catch (FileNotFoundException e) {
                                System.out.println("File not found.");
                                System.err.println("FileNotFoundException: " + e.getMessage());
                            } catch (IOException e) {
                                System.out.println("Problem reading file.");
                            }
                }
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
        // GateUi is no longer managed by this CardLayout
        if (!menu.equals("GateUi")) {
            card.show(menus, menu);
            currentMenu = menu;
            System.out.println("swapping Menu to " + menu);
        } else {
            // If you need to specifically control GateUi visibility, do it here
            // For example: GateUi.setVisible(true); or GateUi.setVisible(false);
            // Currently, it's set to visible and positioned in the constructor.
            System.out.println("Attempted to swap to GateUi, which is now an overlay.");
        }
    }

    /* MouseMotionListener methods */
    public void mouseMoved(MouseEvent e)  { mouseX = e.getX(); mouseY = e.getY(); }
    public void mouseDragged(MouseEvent e){ mouseMoved(e); }
    boolean highscore_saved = false;
	@Override
	public void actionPerformed(ActionEvent e) {
        //Timer goes until all gates open
        if(!mainMap.allOpen) {
            if (timeSec >= 60) {
                timeMin++;
                timeSec = 0;
            }
            timeSec += 0.02;
        }if(mainMap.allOpen){
            if(!highscore_saved) {
                System.out.println("HI");
                highscore_saved = true;
                File dataFile = new File("src/scores.txt");
                FileWriter out;
                BufferedWriter writeFile;
                Scanner input = new Scanner(System.in);
                String name;
                int score;

                try {
                    out = new FileWriter(dataFile);  // Overwrites the file; use 'true' as second arg to append instead
                    writeFile = new BufferedWriter(out);

                    name = "Joe";
                    writeFile.write(name + " " + timeSec);
                    writeFile.newLine();

                    writeFile.close();
                    out.close();
                    System.out.println("High scores written to file.");
                } catch (IOException err) {
                    System.out.println("Problem writing to file.");
                    System.err.println("IOException: " + err.getMessage());
                }
            }
        }
    }
}
