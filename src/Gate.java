import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Gate {
    Boolean working;
    Boolean open;
    int x;
    int y;
    int size;
    Color color;
    JPanel panel;
    int materialsNeeded;
    int playersMaterials = 0; // Materials available to the player
    static int boundary = 30; // Boundary for collision detection
    private BufferedImage gateImage; // Image for gate UI
    private SpriteManager gateManager;

    boolean fake = false;
    DrawingPanel drawing;
    Gate(int x, int y, int size, int materialsNeeded, boolean fake) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
        this.materialsNeeded = materialsNeeded;
        this.panel = new JPanel(new BorderLayout());
        this.fake = fake;
        // Load the gate image
        loadGateImage();

        drawing = new DrawingPanel(this.materialsNeeded, this.working, this.open, this.playersMaterials);
        this.panel.add(drawing); // Add DrawingPanel to center
        this.gateManager = new SpriteManager(32, 32, "resources/sprites/GateSpriteSheet.png");
        this.gateManager.updateState(1);
    }
    Gate(int x, int y, int size, int materialsNeeded) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
        this.materialsNeeded = materialsNeeded;
        this.panel = new JPanel(new BorderLayout());
        // Load the gate image
        loadGateImage();
        
        drawing = new DrawingPanel(this.materialsNeeded, this.working, this.open, this.playersMaterials);
        this.panel.add(drawing); // Add DrawingPanel to center
        this.gateManager = new SpriteManager(32, 32, "resources/sprites/GateSpriteSheet.png");
    }

    private void loadGateImage() {
        try {
            // Try different ways to load the image
            URL resourceUrl = getClass().getResource("/resources/sprites/Gate.png");
            if (resourceUrl != null) {
                gateImage = ImageIO.read(resourceUrl);
            } else {
                // Try alternative loading methods
                resourceUrl = getClass().getClassLoader().getResource("resources/sprites/Gate.png");
                if (resourceUrl != null) {
                    gateImage = ImageIO.read(resourceUrl);
                } else {
                    // Fall back to direct file path
                    File file = new File("resources/sprites/Gate.png");
                    if (file.exists()) {
                        gateImage = ImageIO.read(file);
                    } else {
                        System.out.println("Gate image not found");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load gate image: " + e.getMessage());
        }
    }

    void draw(Graphics g, double x, double y, int size) {
        if (fake) {
            if(this.working){
                if (this.open)
                    gateManager.updateState(4); // Open state
                else
                    gateManager.updateState(1); // Closed state
            }else{
                if (this.open)
                    gateManager.updateState(1); // Open state
                else
                    gateManager.updateState(0); // Closed state
            }
        }else{
            if(this.working){
                if (this.open)
                    gateManager.updateState(3); // Open state
                else
                    gateManager.updateState(2); // Closed state
            }else{
                if (this.open)
                    gateManager.updateState(4); // Open state
                else
                    gateManager.updateState(5); // Closed state
            }
        }
        //g.setColor(color);
        //g.fillRect((int) (this.x * size + x), (int) (this.y * size + y), this.size, this.size);
        gateManager.drawSprite(g, (int) (this.x * size + x - this.size/2), (int) (this.y * size + y - this.size/2), (int)(this.size*1.5), (int)(this.size*1.5));
    }
    public void updateUI() {
        drawing.updateState(this.materialsNeeded, this.working, this.open, this.playersMaterials);
        panel.repaint();
    }
    public Square getSquare(double x, double y) {
        return new Square((int)(this.x * size + x),(int) (this.y * size + y), this.size, this.size, this.x, this.y);
    }
    public Rectangle getCollisionRect(double x, double y, int size) {
        return new Rectangle((int)(this.x * size + x - boundary),(int) (this.y * size + y - boundary), this.size + boundary*2, this.size + boundary*2);
    }

    Rectangle getrect(double x, double y, int size) {
        return new Rectangle((int) (this.x * size + x), (int) (this.y * size + y), size, size);
    }
    boolean getWorking() {
        return working;
    }
    int getMaterialsNeeded(){
        return this.materialsNeeded;
    }
    void setWorking(boolean working) {
        this.working = working;
        updateUI();
    }

    void setOpen(boolean open) {
        this.open = open;
        updateUI();
    }

    void setMaterials(int materials) {
        this.playersMaterials = materials;
        updateUI();
    }


    void setPlayersMaterials(int materials) {
        this.playersMaterials = materials;
    }
    JPanel getPanel() {
        drawing.updateState(this.materialsNeeded, this.working, this.open, this.playersMaterials);
        panel.repaint();
        return panel;
    }

    private class DrawingPanel extends JPanel {
        int materialsNeeded, playersMaterials;
        boolean working;
        boolean open;
        DrawingPanel(int materialsNeeded, boolean working, boolean open, int playersMaterials) {
            setBackground(Color.lightGray); // Set background color
            this.materialsNeeded = materialsNeeded;
            this.working = working;
            this.open = open;
            this.playersMaterials = playersMaterials;
        }

        public void updateState(int materialsNeeded, boolean working, boolean open, int playersMaterials) {
            this.materialsNeeded = materialsNeeded;
            this.working = working;
            this.open = open;
            this.playersMaterials = playersMaterials;
            repaint();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Draw the background image
            if (gateImage != null) {
                g.drawImage(gateImage, 0, 0, getWidth(), getHeight(), this);
            }
            
            // Draw text with higher contrast for better visibility over the image
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            
            // Draw with black outline for better visibility
            drawStringWithOutline(g, "Materials needed: " + this.materialsNeeded, 30, 40);
            
            drawStringWithOutline(g, "B = Pay Up Materials", 30, 80);

            drawStringWithOutline(g, "C to Open/Close Gate", 30, 100);
        }
        
        // Helper method to draw text with an outline for better visibility on image background
        private void drawStringWithOutline(Graphics g, String text, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.BLACK);
            g2.drawString(text, x-1, y-1);
            g2.drawString(text, x-1, y+1);
            g2.drawString(text, x+1, y-1);
            g2.drawString(text, x+1, y+1);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);
        }
    }
}