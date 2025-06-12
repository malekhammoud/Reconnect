import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;

public class playerMotion extends JFrame implements KeyListener, MouseMotionListener, ActionListener {
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;
    static CardLayout card = new CardLayout();
    JPanel menus, title;
    JPanel GateUi;
    String currentMenu;
    DrawingPanel Drawing_title;
    Timer timer;
    static int TIMESPEED = 10;
    double timeSec;
    int counter = 0;
    int timeMin;
    static int hp = 3, setMain = 0;
    SpriteManager healthUI = new SpriteManager(256,256, "resources/sprites/HealthUISprites.png");


    JLayeredPane layeredPane; // Added JLayeredPane

    Map mainMap = new Map(-350, -650, 25, 0.1);

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
        new playerMotion();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (currentMenu.equals("MainGame")) {
            if (key == KeyEvent.VK_W) {
                mainMap.movedown();
                dirX = 0;
                dirY = -1;
                // update sprite direction
                player.spriteManager.setDirection(dirX, dirY);
            }
            if (key == KeyEvent.VK_L && (setMain == 0 || setMain == 2 || setMain == 3 || setMain == 4 || mainMap.allOpen || hp <= 0)) {
                dispose();
                System.exit(0);
            }
            if (key == KeyEvent.VK_A) {
                mainMap.moveright();
                dirX = -1;
                dirY = 0;
                // update sprite direction
                player.spriteManager.setDirection(dirX, dirY);
            }
            if (key == KeyEvent.VK_S) {
                mainMap.moveup();
                dirX = 0;
                dirY = 1;
                // update sprite direction
                player.spriteManager.setDirection(dirX, dirY);
            }
            if (key == KeyEvent.VK_D) {
                mainMap.moveleft();
                dirX = 1;
                dirY = 0;
                // update sprite direction
                player.spriteManager.setDirection(dirX, dirY);
            }
            /* Shoot along last movement vector */
            if (key == KeyEvent.VK_J) {
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
            if (key == KeyEvent.VK_I) {
                mainMap.payGate = true;
            }
            if (key == KeyEvent.VK_O) {
                mainMap.openGate = true;
            }
            if (key == KeyEvent.VK_L) {
                SwapMenuTo("Map");
                timer.stop();
            }
            if (key == KeyEvent.VK_K) {
                System.out.println("HIII");
                SwapMenuTo("Pause");
                setMain = 4;
            }
            if (key == KeyEvent.VK_M) {
                SwapMenuTo("MainGame");
                timer.start();
            }
        }
        if (currentMenu.equals("Map")) {
            if (key == KeyEvent.VK_U) {
                timer.start();
                SwapMenuTo("MainGame");
            }
        }
        if (currentMenu.equals("Pause")) {
            if (key == KeyEvent.VK_U) {
                setMain = 1;
                SwapMenuTo("MainGame");
                timer.start();
            }
            if (key == KeyEvent.VK_J) {
                setMain = 0;
                SwapMenuTo("MainGame");
                timer.start();
                resetGame();
            }
            if (key == KeyEvent.VK_L) {
                dispose();
                System.exit(0);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Movement key handling
        if (key == KeyEvent.VK_W) {
            mainMap.vy[1] = 0;
        }
        if (key == KeyEvent.VK_A) {
            mainMap.vx[1] = 0;
        }
        if (key == KeyEvent.VK_S) {
            mainMap.vy[0] = 0;
        }
        if (key == KeyEvent.VK_D) {
            mainMap.vx[0] = 0;
        }

        // Menu navigation
        if (key == KeyEvent.VK_U) {
            if (setMain == 2 || setMain == 3) { // If in highscores or info menu
                setMain = 0; // Return to main menu
                mainMap.payGate = false;
            } else if (setMain == 0) { // Only start game if already on main menu
                setMain = 1;
                mainMap.payGate = false;
                // Make sure timer is running when starting a new game
                timer.start();
            }
        }
        if (key == KeyEvent.VK_I) mainMap.openGate = false;
        if (key == KeyEvent.VK_I && setMain == 0) setMain = 2;
        if (key == KeyEvent.VK_O && setMain == 0) setMain = 3;

        // Game over restart handling
        if (hp <= 0 && key == KeyEvent.VK_U) {
            setMain = 0;
            SwapMenuTo("MainGame");
            resetGame();
            timer.start(); // Make sure to restart the timer
        }

        // Victory screen restart handling
        if (mainMap.allOpen && key == KeyEvent.VK_U) {
            setMain = 0;
            SwapMenuTo("MainGame");
            resetGame();
            highscore_saved = false;
            timer.start(); // Make sure to restart the timer
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    playerMotion() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Reconnect");
        setSize(500, 500);                    // frame stays 500×500
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(this.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setUndecorated(true);

        layeredPane = new JLayeredPane();
        /*
        layeredPane.setBackground(new Color(20, 20, 50));
        layeredPane.setOpaque(true);
         */

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
        Drawing_title = new DrawingPanel(0);
        DrawingPanel Drawing_p = new DrawingPanel(1);
        DrawingPanel Drawing_q = new DrawingPanel(2);
        DrawingPanel Drawing_b = new DrawingPanel(3);

        panel.add(Drawing_p);
        inventoryMenu.add(Drawing_q);
        mapMenu.add(Drawing_b);
        int x = (screenSize.width - WIDTH) / 2;
        int y = (screenSize.height - HEIGHT)/ 2;
        menus.setBounds(x, y, WIDTH, HEIGHT);
        layeredPane.add(menus, JLayeredPane.PALETTE_LAYER);

        // The main game panel
        BackgroundPanel background = new BackgroundPanel(1);
        background.setBounds(0, 0, screenSize.width, screenSize.height);
        Drawing_title.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(Drawing_title, JLayeredPane.POPUP_LAYER);
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
        player = new Player(505, 240, 2, 2, 0.3,
                new Color(0, 0, 0),
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

        // Draw a circuit board pattern in the background
        g.setColor(new Color(0, 80, 40));
        for (int i = 0; i < getWidth(); i += 50) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += 50) {
            g.drawLine(0, i, getWidth(), i);
        }

    }
    private class BackgroundPanel extends JPanel{
        int panel;
        BackgroundPanel(int pan) {
            setOpaque(true);
            this.panel = pan;
            setBackground(Color.BLACK); // Set a dark background color
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (panel == 1 && setMain == 0) {
                // Title screen - no special background needed
            } else if(setMain == 1) {
                if(hp <= 0) {
                    // Make sure background is black for game over
                    setBackground(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Monospaced", Font.BOLD, 24));
                    g.drawString("Inventory: " + player.getInventory(), 50, 50);
                    String timeSecString = String.format("%.2f", timeSec);
                    g.drawString("Time :" + timeMin + ":" + timeSecString, 50, 80);
                }
            }
        }
    }

    private class DrawingPanel extends JPanel {
        int panel;
        Font font = new Font("Serif", Font.BOLD, 24);
        DrawingPanel(int pan) { setPreferredSize(new Dimension(1050, 785)); panel = pan;}
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            //Title screen
            if(setMain == 0 && panel == 0) {
                BufferedImage title = loadImage("resources/sprites/Title.png");
                g.drawImage(title, 0, 0, getWidth(), getHeight(), null);
            }

            // Draw motherboard background pattern when in game mode
            if (panel == 1 && setMain == 1) {
                drawMotherboardBackground(g);
            }

            // High Score screen
            if(setMain == 2 && panel == 0) {
                // Draw Highscore image
                BufferedImage highScore = loadImage("resources/sprites/Highscore.png");
                g.drawImage(highScore, 0,0, getWidth(), getHeight(), null);

                // Draw the score
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 60));
                File scoreFile = new File("scores.txt");
                FileReader in;
                BufferedReader readFile;

                String line;
                try {
                    in = new FileReader(scoreFile);
                    readFile = new BufferedReader(in);
                    line = readFile.readLine();
                    String[] parts = line.split(" ");
                    String name = parts[0];
                    float score = Float.parseFloat(parts[1]);
                    readFile.close();
                    in.close();

                    //Using Font Metrics to center the score text
                    FontMetrics metrics = g.getFontMetrics();
                    String scoreText = String.valueOf(score) + "sec";
                    int textWidth = metrics.stringWidth(scoreText);
                    int textHeight = metrics.getHeight();

                    // Draw string with adjusted coordinates for center alignment
                    g.drawString(scoreText, (getWidth() - textWidth)/2, (getHeight() + textHeight/2)/2);
                } catch (FileNotFoundException e) {
                    System.out.println("File not found.");
                    System.err.println("FileNotFoundException: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Problem reading file.");
                }
            }

            // Info screen
            if(setMain == 3 && panel == 0) {
                // Draw Info image
                BufferedImage infoImage = loadImage("resources/sprites/Info.png");
                g.drawImage(infoImage, 0, 0, getWidth(), getHeight(), null);
                
                // Draw exit instruction
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                
                // Using Font Metrics to center the text
                FontMetrics metrics = g.getFontMetrics();
                String exitText = "Press U to return to menu";
                int textWidth = metrics.stringWidth(exitText);
                
                // Draw string at the bottom of the screen
                g.drawString(exitText, (getWidth() - textWidth)/2, getHeight() - 50);
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
                /*
                g.setColor(Color.WHITE);
                g.drawString("Materials Count: " + player.getInventory(), 10, 15);
                g.drawString("Timer: " + timeSec, 10, 35);
                //Displays time
                String timeSecString = String.format("%.2f", timeSec);
                g.drawString("Time :" + timeMin + ":" + timeSecString, 10, 35);
                 */

                // Draw HP indicator
                if(hp == 3) {healthUI.updateState(0);}
                if(hp == 2) {healthUI.updateState(1);}
                if(hp == 1) {healthUI.updateState(2);}

                // Game over screen with improved visibility
                if(hp <= 0) {
                    healthUI.updateState(3);

                    // Fill the entire panel with black background
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());

                    // Draw game over image
                    BufferedImage gameOver = loadImage("resources/sprites/GameOver.png");
                    if (gameOver != null) {
                        g.drawImage(gameOver, 250, -20, 550, 550, null);
                    }

                    // Draw instructions
                    g.setFont(new Font("Arial", Font.BOLD, 24));
                    g.drawString("Press U to restart game", 380, 550);

                    // Draw final stats
                    g.setFont(new Font("Arial", Font.PLAIN, 20));
                    String timeSecString = String.format("%.2f", timeSec);
                    g.drawString("Time: " + timeMin + ":" + timeSecString, 380, 600);
                }

                if (hp > 0) {
                    healthUI.updateCurrentSprite();
                    healthUI.drawSprite(g, 280, 400, 160, 160);
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
            if (panel == 3 && setMain == 1) {
                // Draw the main map at a smaller scale instead of menuMap
                mainMap.draw(g, 2);  // Using the new scaled draw method

                g.setColor(Color.BLACK);
                g.setFont(font);
                g.drawString("Map View", 400, 40);
            }

            if (panel == 0 && setMain == 4){
                BufferedImage highScore = loadImage("resources/sprites/Paused.png");
                g.drawImage(highScore, 0,0, getWidth(), getHeight(), null);
                timer.stop();
            }
            if(panel == 0 && mainMap.allOpen){
                BufferedImage highScore = loadImage("resources/sprites/Win.png");
                g.drawImage(highScore, 0,0, getWidth(), getHeight(), null);
                g.setColor(Color.WHITE);
                Font winFont = new Font("Consolas", Font.BOLD, 80);
                g.setFont(winFont);

                String timeSecString = String.format("%.2f", timeSec);

                String timeText = "Time: " + timeMin + ":" + timeSecString;
                FontMetrics metrics = g.getFontMetrics(winFont);
                int textWidth = metrics.stringWidth(timeText);
                g.drawString(timeText, (getWidth() - textWidth)/2, getHeight()/4-70);
                timer.stop();
            }
        }
    }

    public void playAnimation() {
        while (true) {
            // Stop game updates if game over or win screen is active
            boolean gameActive = setMain == 1 && hp > 0 && !mainMap.allOpen;

            if(counter%10 == 0) {
                if (mainMap.gateUiClose() && !mainMap.allOpen && hp>0) {
                    layeredPane.remove(GateUi);
                    GateUi = mainMap.getGateUi();
                    GateUi.setBounds(800, 300, 250, 130); // Increased width from 150 to 250
                    layeredPane.add(GateUi, JLayeredPane.POPUP_LAYER);
                    GateUi.setVisible(true);
                    layeredPane.repaint();
                } else {
                    GateUi.setVisible(false);
                }
                Drawing_title.setVisible(setMain == 0 || setMain == 2 || setMain == 3 || setMain == 4 || mainMap.allOpen); // Show title, high score, info, or pause screen
            }
            if(hp<=0){
                GateUi.setVisible(false);
            }

            // Only process game movement if game is active
            if (gameActive) {
                mainMap.move(player);
                if (currentMenu.equals("MainGame")) {
                    player.update(WIDTH, HEIGHT, mainMap.size);     // ← advance bullets and update player size to map
                    /* quick bullet-vs-enemy check – remove bullet + enemy cell */
                    Iterator<Player.Bullet> it = player.bullets.iterator();
                    while (it.hasNext()) {
                        Player.Bullet b = it.next();
                        if (mainMap.killEnemyAt(b.getX(), b.getY())) it.remove();
                    }
                }
            }
            
            Toolkit.getDefaultToolkit().sync();
            if (GateUi != null) {
                GateUi.repaint();
            }
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
        //Timer goes until all gates open or game ends
        counter++;
        
        // Only update game time if the game is active
        if (!mainMap.allOpen && hp > 0 && setMain == 1) {
            if (timeSec >= 60) {
                timeMin++;
                timeSec = 0;
            }
            timeSec += 0.02;
        }
        
        // Handle game completion
if (mainMap.allOpen && !highscore_saved) {
    highscore_saved = true;
    File dataFile = new File("scores.txt");

    // Variables for file operations
    FileReader in = null;
    BufferedReader readFile = null;
    FileWriter out = null;
    BufferedWriter writeFile = null;

    boolean shouldSaveScore = true;
    float currentHighScore = Float.MAX_VALUE; // Initialize to max value
    String highScoreName = "Joe";

    // First, try to read the current highscore (if it exists)
    if (dataFile.exists()) {
        try {
            in = new FileReader(dataFile);
            readFile = new BufferedReader(in);
            String line = readFile.readLine();
            if (line != null) {
                String[] parts = line.split(" ");
                highScoreName = parts[0];
                currentHighScore = Float.parseFloat(parts[1]);

                // Compare with current score (lower time is better)
                if (timeSec >= currentHighScore) {
                    shouldSaveScore = false; // Current score is not better
                    System.out.println("Current score (" + timeSec+
                                      ") is not better than the highscore (" + currentHighScore + ")");
                }
            }
        } catch (IOException | NumberFormatException err) {
            System.out.println("Error reading highscore file: " + err.getMessage());
        } finally {
            try {
                if (readFile != null) readFile.close();
                if (in != null) in.close();
            } catch (IOException err) {
                System.out.println("Error closing file readers");
            }
        }
    }

    // Only save if the current score is better or there's no existing score
    if (shouldSaveScore) {
        try {
            out = new FileWriter(dataFile);
            writeFile = new BufferedWriter(out);

            String name = "Player"; // Using a default name
            writeFile.write(name + " " + timeSec);
            writeFile.newLine();

            writeFile.close();
            out.close();
            System.out.println("New highscore written to file: " + timeSec);
        } catch (IOException err) {
            System.out.println("Problem writing to file.");
            System.err.println("IOException: " + err.getMessage());
        }
    }
}
        
        // Also stop the timer when the game is over
        if (hp <= 0 && setMain == 1) {
            timer.stop();
        }
    }
    static BufferedImage loadImage(String filename) {
        BufferedImage img = null;
        try {
            // Use Class.getResource which handles resource folders properly
            URL resourceUrl = playerMotion.class.getResource("/" + filename);
            if (resourceUrl != null) {
                img = ImageIO.read(resourceUrl);
            } else {
                System.out.println("Resource not found: " + filename);
                // Try alternative loading methods
                // 1. Try using ClassLoader
                resourceUrl = playerMotion.class.getClassLoader().getResource(filename);
                if (resourceUrl != null) {
                    img = ImageIO.read(resourceUrl);
                } else {
                    // 2. Fall back to file path as last resort
                    File file = new File(filename);
                    if (file.exists()) {
                        img = ImageIO.read(file);
                    } else {
                        System.out.println("File not found: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load image: " + filename + " - " + e.getMessage());
            e.printStackTrace();
        }
        return img;
    }
}
