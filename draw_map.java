import javax.swing.*;
import java.awt.*;

public class draw_map extends JFrame {
    int[][] main_map= { {0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
    void drawmap(Graphics g, int[][] maps){
        int size = 10;
        for (int y = 0; y < maps.length; y++){
            for (int x = 0; x < maps[y].length; x++){
                if (main_map[y][x] == 0){
                    g.setColor(Color.BLACK);
                    g.fillRect(x*size, y*size, size,size);
                };
            }
        }

    }
    public static void main(String[] args) {
        new draw_map();
    }
    draw_map(){
        //Create the Jframe
        this.setTitle("HELLOW WORLD");
        this.setSize(500,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        draw_map.DrawingPanel p = new draw_map.DrawingPanel();
        panel.add(p);
        //this.setUndecorated(true);
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }
    private class DrawingPanel extends JPanel {
        DrawingPanel(){
            this.setPreferredSize(new Dimension(500,500));
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            drawmap(g, main_map);
        }
    }
}