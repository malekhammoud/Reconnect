import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class draw_map extends JFrame  implements MouseListener, MouseMotionListener, KeyListener {
    int height = 200;
    int width= 200;
    int value = 1;
    /*
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
     */
    int[][] main_map = new int[height][width];
    int size = 10;
    void drawmap(Graphics g, int[][] maps){
        for (int y = 0; y < maps.length; y++){
            for (int x = 0; x < maps[y].length; x++){
                if (main_map[y][x] ==1){
                    g.setColor(Color.BLACK);
                    g.fillRect(x*size, y*size-40, size,size);
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
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        main_map[e.getY()/size][e.getX()/size] = value;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key= e.getKeyCode();
        if (key == KeyEvent.VK_D){
            value = 0;
        }
        if (key == KeyEvent.VK_SPACE){
            for (int y = 0; y < main_map.length; y++) {
                for (int x = 0; x < main_map[y].length; x++) {
                    System.out.print(main_map[y][x] + " ");
                }
                System.out.println();
            }
        }
        else{
            value = 1;
        }

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

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