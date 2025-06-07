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
    static int boundary = 30; // Boundary for collision detection

    Gate(int x, int y, int size, int materialsNeeded) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
        this.materialsNeeded = materialsNeeded;
        this.panel = new JPanel(new BorderLayout());
        DrawingPanel Drawing = new DrawingPanel(this.materialsNeeded);
        this.panel.add(Drawing); // Add DrawingPanel to center
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
    public Square getSquare(double x, double y) {
        return new Square((int)(this.x * size + x),(int) (this.y * size + y), this.size, this.size, this.x, this.y);
    }
    public Rectangle getCollisionRect(double x, double y, int size) {
        return new Rectangle((int)(this.x * size + x - boundary),(int) (this.y * size + y - boundary), this.size + boundary*2, this.size + boundary*2);
    }

    Rectangle getrect(double x, double y, int size) {
        return new Rectangle((int) (this.x * size + x), (int) (this.y * size + y), size, size);
    }

    JPanel getPanel() {
        return this.panel;
    }

    private class DrawingPanel extends JPanel {
        int materialsNeeded;
        DrawingPanel(int materialsNeeded) {
            setBackground(Color.lightGray); // Set background color
            this.materialsNeeded = materialsNeeded;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawString("Materials needed: " + this.materialsNeeded, 10, 15);
            g.drawString("I = Pay all" + this.materialsNeeded, 10, 30);
            g.drawString("U = Pay 1" + this.materialsNeeded, 10, 45);
            g.drawString("O = Open gate", 10, 55);
            g.drawString("C = Close gate", 10, 70);
        }
    }
}
