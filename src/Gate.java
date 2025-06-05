import javax.swing.*;
import java.awt.*;

public class Gate {
    Boolean working;
    Boolean open;
    int x;
    int y;
    int size;
    Color color;
    JPanel panel; // Changed: initialized in constructor with BorderLayout


    Gate(int x, int y, int size, CardLayout card) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
        this.panel = new JPanel(new BorderLayout()); // Use BorderLayout
        DrawingPanel Drawing = new DrawingPanel();
        this.panel.add(Drawing, BorderLayout.CENTER); // Add DrawingPanel to center
    }

    void show() {
        panel.setVisible(true);
        panel.repaint();
        panel.show();
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

    Rectangle getrect(double x, double y, int size) {
        return new Rectangle((int) (this.x * size + x), (int) (this.y * size + y), size, size);
    }

    JPanel getPanel() {
        return panel;
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel() {
            setPreferredSize(new Dimension(50, 50)); // Set preferred size for the panel
            setBackground(Color.GRAY); // Set background color
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.red);
            g.fillRect(0, 0, getWidth(), getHeight()); // Fill the component's actual bounds
        }
    }
}
