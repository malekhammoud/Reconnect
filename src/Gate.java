import javax.swing.*;
import java.awt.*;

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

    DrawingPanel drawing;
    Gate(int x, int y, int size, int materialsNeeded) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
        this.materialsNeeded = materialsNeeded;
        this.panel = new JPanel(new BorderLayout());
        drawing = new DrawingPanel(this.materialsNeeded, this.working, this.open, this.playersMaterials);
        this.panel.add(drawing); // Add DrawingPanel to center
    }

    void draw(Graphics g, double x, double y, int size) {
        if (this.working) {
            this.color = Color.ORANGE;
            if (this.open) {
                this.color = Color.green;
            }
        } else {
            this.color = Color.red;
        }
        g.setColor(color);
        g.fillRect((int) (this.x * size + x), (int) (this.y * size + y), this.size, this.size);
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
        System.out.println(this.working);
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
            g.setColor(Color.BLACK);
           // if(!working) {
            //    g.drawString("Gate is not working", 10, 15);
                g.drawString("Materials needed: " + this.materialsNeeded, 10, 25);
                if (playersMaterials >= this.materialsNeeded) {
                    g.drawString("C = Pay " + this.materialsNeeded + " materials", 10, 45);
                } else {
                    g.drawString("C = Not enough materials!!!", 10, 45);
                }
            g.drawString("o to open/close", 10, 60);
                /*
            }else {
                g.drawString("Gate is working!!!!", 10, 15);
                if (open) {
                    g.drawString("Gate is open, o to close", 10, 60);
                } else {
                    g.drawString("Gate is closed o to open", 10, 60);
                }
            }

                 */
        }
    }
}
