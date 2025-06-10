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
    JPanel menus, title;
    JPanel GateUi;
    String currentMenu;
    Timer timer;
    static int TIMESPEED = 10;
    double timeSec;
    int timeMin;
    static int hp = 3, setMain = 0;

    JLayeredPane layeredPane; // Added JLayeredPane

    Map mainMap = new Map(-350, -650, 25, 0.1);

    Map menuMap = new Map(100, 74, 4, 0.1);
    // Adjust player starting position to be within the map bounds
    Player player = new Player(505, 240, 2, 2, 0.3,
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
        if (key == KeyEvent.VK_UP) {
            mainMap.movedown();
            menuMap.movedown();
            dirX = 0;
            dirY = -1;
            // update sprite direction
            player.spriteManager.setDirection(dirX, dirY);
        }
        if (key == KeyEvent.VK_LEFT) {
            mainMap.moveright();
            menuMap.moveright();
            dirX = -1;
            dirY = 0;
            // update sprite direction
            player.spriteManager.setDirection(dirX, dirY);
        }
        if (key == KeyEvent.VK_DOWN) {
            mainMap.moveup();
            menuMap.moveup();
            dirX = 0;
            dirY = 1;
            // update sprite direction
            player.spriteManager.setDirection(dirX, dirY);
        }
        if (key == KeyEvent.VK_RIGHT) {
            mainMap.moveleft();
            menuMap.moveleft();
            dirX = 1;
            dirY = 0;
            // update sprite direction
            player.spriteManager.setDirection(dirX, dirY);
        }
        /* SPACE: shoot along last movement vector */
        if (key == KeyEvent.VK_SPACE) {
            // Make sure we have valid direction vectors
            if (dirX == 0 && dirY == 0) {
                // Default to shooting up if no direction
                dirX = 0;
                dirY = -1;
            }
            player.shoot(dirX, dirY);
            // Debug output
            System.out.println("Shot bullet in direction: " + dirX + ", " + dirY);
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
        if (key == KeyEvent.VK_H) {
            SwapMenuTo("Pause");
            timer.stop();
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
        if (currentMenu.equals("Pause")){

            if (key == KeyEvent.VK_M) {
                SwapMenuTo("MainGame");
                timer.start();
            }
            if (key == KeyEvent.VK_B) {
                setMain = 0;
                SwapMenuTo("MainGame");
                timer.start();
                resetGame();
            }
            if (key == KeyEvent.VK_X) {

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
        if (key == KeyEvent.VK_U) {
            if (setMain == 2) { // If in highscores menu
                setMain = 0; // Return to main menu
                mainMap.payGate = false;
            } else if (setMain == 0) { // Only start game if already on main menu
                setMain = 1;
                mainMap.payGate = false;
            }
        }
        if (key == KeyEvent.VK_I) mainMap.openGate = false;
        if (key == KeyEvent.VK_I && setMain == 0) setMain = 2;
        if (key == KeyEvent.VK_O && setMain == 0) setMain = 3;
        if(hp <= 0 && key == KeyEvent.VK_U) {
            setMain = 0;
            SwapMenuTo("MainGame");
            timer.start();
            resetGame();
        }}

        @Override
        public void keyTyped(KeyEvent e) {}

    playerMotion() {
        setTitle("Reconnect");
        setSize(500, 500);                    // frame stays 500×500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(this.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        layeredPane = new JLayeredPane();
        //chaning to grey for debugging
        layeredPane.setBackground(Color.GRAY);
        layeredPane.setOpaque(true);
        //layeredPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        menus = new JPanel(card);
        JPanel panel = new JPanel();
        JPanel mapMenu = new JPanel();
        JPanel inventoryMenu = new JPanel();
        JPanel pauseMenu = new JPanel();
        JPanel title = new JPanel();

        panel.setBackground(Color.BLACK);
        GateUi = mainMap.getGateUi(); // Use a layout manager


        menus.add(panel,"MainGame");
        menus.add(mapMenu,"Map");
        menus.add(inventoryMenu,"Inventory");
        menus.add(pauseMenu,"Pause");
        DrawingPanel Drawing_p = new DrawingPanel(1);
        DrawingPanel Drawing_q = new DrawingPanel(2);
        DrawingPanel Drawing_b = new DrawingPanel(3);
        DrawingPanel Drawing_d = new DrawingPanel(4);

        menuMap.mapScrub();

        panel.add(Drawing_p);
        inventoryMenu.add(Drawing_q);
        mapMenu.add(Drawing_b);
        pauseMenu.add(Drawing_d);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - WIDTH) / 2;
        int y = (screenSize.height - HEIGHT)/ 2;
        menus.setBounds(x, y, WIDTH, HEIGHT);
        layeredPane.add(menus, JLayeredPane.DEFAULT_LAYER);

        setContentPane(layeredPane);
        SwapMenuTo("MainGame");

        //add(menus);
        // pack();                            // ← removed: keeps window from shrinking

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

    void resetGame() {
        mainMap = new Map(-40, -40, 25, 0.1);
        menuMap = new Map(128, 128, 4, 0.1);
        player = new Player(505, 240, 2, 2, 0.3,
                new Color(0, 0, 0),
                new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));
        Ghost = new Player(WIDTH / 2 - 10, HEIGHT / 2 - 10, 2, 2, 0.3,
                new Color(253, 212, 6),
                new Rectangle(WIDTH / 2 - bounds / 2, HEIGHT / 2 - bounds / 2, bounds, bounds));
        timer.restart();
        timeSec=0;
        timeMin=0;
        hp = 3;
    }

    // Method to draw a cartoon motherboard pattern background that moves with the map
    private void drawMotherboardBackground(Graphics g) {
        // Get the current map offset to make background move with the map
        int offsetX = (int)mainMap.x;
        int offsetY = (int)mainMap.y;

        // Base background color (dark green/blue typical of circuit boards)
        g.setColor(new Color(0, 50, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw circuit traces (lighter green lines)
        g.setColor(new Color(0, 200, 100));

        // Horizontal traces - adjust position based on map offset
        for (int y = 20; y < getHeight() + Math.abs(offsetY); y += 80) {
            int drawY = (y + offsetY) % getHeight();
            if (drawY < 0) drawY += getHeight();
            g.fillRect(0, drawY, getWidth(), 3);
        }

        // Vertical traces - adjust position based on map offset
        for (int x = 25; x < getWidth() + Math.abs(offsetX); x += 75) {
            int drawX = (x + offsetX) % getWidth();
            if (drawX < 0) drawX += getWidth();
            g.fillRect(drawX, 0, 2, getHeight());
        }

        // Add some diagonal traces
        g.setColor(new Color(0, 180, 80));
        for (int i = 0; i < 15; i++) {
            int x1 = ((i * 100) + offsetX) % getWidth();
            if (x1 < 0) x1 += getWidth();
            int y1 = (i * 50 + offsetY) % getHeight();
            if (y1 < 0) y1 += getHeight();
            int x2 = ((i * 100 + 200) + offsetX) % getWidth();
            if (x2 < 0) x2 += getWidth();
            int y2 = ((i * 50 + 200) + offsetY) % getHeight();
            if (y2 < 0) y2 += getHeight();
            g.drawLine(x1, y1, x2, y2);
        }

        // Add circuit "pads" (small circles) - adjust position based on map offset
        g.setColor(new Color(220, 220, 0));
        for (int x = 0; x < getWidth() + 100; x += 100) {
            for (int y = 0; y < getHeight() + 100; y += 100) {
                int drawX = (x + offsetX) % getWidth();
                if (drawX < 0) drawX += getWidth();
                int drawY = (y + offsetY) % getHeight();
                if (drawY < 0) drawY += getHeight();
                g.fillOval(drawX, drawY, 10, 10);
            }
        }

        // Add some IC chips - adjust position based on map offset
        g.setColor(new Color(50, 50, 50));
        for (int i = 0; i < 8; i++) {
            int x = ((150 + i * 180) + offsetX) % getWidth();
            if (x < 0) x += getWidth();
            int y = ((120 + (i % 3) * 160) + offsetY) % getHeight();
            if (y < 0) y += getHeight();

            // Only draw chips if they're in the visible area
            if (x < getWidth() - 60 && y < getHeight() - 30) {
                g.fillRect(x, y, 60, 30);

                // Pins on chips
                g.setColor(new Color(200, 200, 200));
                for (int p = 0; p < 8; p++) {
                    if (y - 3 >= 0) g.fillRect(x + 5 + p * 7, y - 3, 2, 3);
                    if (y + 30 < getHeight()) g.fillRect(x + 5 + p * 7, y + 30, 2, 3);
                }
                g.setColor(new Color(50, 50, 50));
            }
        }

        // Add connections between components
        g.setColor(new Color(0, 150, 70));
        for (int i = 0; i < 15; i++) {
            int x1 = (int)(Math.random() * getWidth());
            int y1 = (int)(Math.random() * getHeight());
            int x2 = x1 + (int)(Math.random() * 200) - 100;
            int y2 = y1 + (int)(Math.random() * 200) - 100;
            g.drawLine(x1, y1, x2, y2);
        }
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

            // Draw motherboard background pattern when in game mode
            if (panel == 1 && setMain == 1) {
                drawMotherboardBackground(g);
            }

            //Title screen

           if(setMain == 0) {
               g.setColor(Color.BLACK);
               g.setFont(new Font("Arial", Font.BOLD, 32));
               g.drawString("Reconnect :)", 300, 50);
               g.drawString("Press U(a) to start game", 300, 100);
               g.drawString("Press I(b) to see highscores", 300,  150);
           }

           //Display highscores
           if(setMain == 2) {
               g.setColor(Color.BLACK);
               g.setFont(new Font("Arial", Font.BOLD, 32));
               g.drawString("High Scores", 300, 50);
               g.drawString("Press U to return to main menu", 300, 150);  // Changed instruction text
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
                       g.drawString("High score: " + topScorer + " with " + highScore, 300, 80);
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


            if (panel == 1 && setMain == 1){
                // First draw the map
                mainMap.draw(g, 1);

                // Draw the player
                player.drawSingle(g); // Just draw the player without bullets

                // Draw the black borders around the game area
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 390, 1050);
                g.fillRect(685, 0, 690, 1050);
                g.fillRect(0, 380, 1050, 520);
                g.fillRect(0, 0, 1050, 160);
                // Now draw the bullets ON TOP of everything else
                for (Player.Bullet bullet : player.bullets) {
                    bullet.draw(g);
                }

                // Draw UI elements
                g.setColor(Color.WHITE);
                g.drawString("Materials Count: " + player.getInventory(), 10, 15);
                //Displays time
                String timeSecString = String.format("%.2f", timeSec);
                g.drawString("Time :" + timeMin + ":" + timeSecString, 10, 35);

                // Draw HP indicator
                if(hp == 3) {g.setColor(Color.GREEN);}
                if(hp == 2) {g.setColor(Color.YELLOW);}
                if(hp == 1) {g.setColor(Color.RED);}
                if(hp <= 0) {
                    //Gameover
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, 1050, 785);
                    g.setColor(Color.BLACK);
                    g.drawString("Game Over :(", 30, 35);
                    g.drawString("Press U(a) to return to title screen", 30, 60);
                    g.setColor(Color.RED);

                }
                g.fillRect(30, 650, 80, 100);
                if(mainMap.allOpen){
                    g.setColor(Color.GREEN);
                    g.fillRect(0, 0, 1, 785);
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
                g.drawString("Materials Count: " + player.getInventory(), 300, 238);
            }

            //Display minimap
            if (panel == 3 && setMain == 1){
                menuMap.draw(g,1);
                Ghost.draw(g);
            }

            if (panel == 4 && setMain == 1){
                g.drawString("press b to go back to menu", 300, 100);
                g.drawString("press m to resume game", 300, 150);
                g.setFont(font);
                g.drawString("!! PAUSED !!", 300, 200);
            }
        }
    }

    public void playAnimation() {
        while (true) {
            if (mainMap.gateUiClose())  {
                layeredPane.remove(GateUi);
                GateUi = mainMap.getGateUi();
                GateUi.setBounds(100, 100, 150, 130);
                layeredPane.add(GateUi, JLayeredPane.POPUP_LAYER);
                GateUi.setVisible(true);
                layeredPane.repaint();
            }else{
                GateUi.setVisible(false);
            }
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
            GateUi.repaint();
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

