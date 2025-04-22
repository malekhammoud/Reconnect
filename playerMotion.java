import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

public class playerMotion extends JFrame implements KeyListener{
    public int WIDTH = 500;
    public int HEIGHT = 500;
    static final int bounds = 70;

    Map mainMap = new Map(-100, -100, 10,  1);
    Player player = new Player(WIDTH/2-10, HEIGHT/2-10, 20, 20, 0.3, new Color(0,0,0), new Rectangle(WIDTH/2-bounds/2, HEIGHT/2-bounds/2, bounds, bounds));
    Enemy enemy = new Enemy(100, 100, 20, 20, 5);
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static void main(String[] args) {
        new playerMotion();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_UP){
            mainMap.movedown();
        }
        if(key == KeyEvent.VK_LEFT){
            mainMap.moveright();
        }
        if(key == KeyEvent.VK_DOWN){
            mainMap.moveup();
        }
        if(key == KeyEvent.VK_RIGHT){
            mainMap.moveleft();
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_UP){
            mainMap.vy[1]=0;
        }
        if(key == KeyEvent.VK_LEFT){
            mainMap.vx[1]=0;
        }
        if(key == KeyEvent.VK_DOWN){
            mainMap.vy[0]=0;
        }
        if(key == KeyEvent.VK_RIGHT){
            mainMap.vx[0]=0;
        }
    }
    public void keyTyped(KeyEvent e) {}
    playerMotion(){
        //Create the Jframe
        this.setTitle("goodbye world");
        this.setSize(500,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        //this.setUndecorated(true);

        JPanel panel = new JPanel();
        DrawingPanel p = new DrawingPanel();
        panel.add(p);
        this.add(panel);
        this.pack();
        this.setVisible(true);
        this.playAnimation();
    }
    
    void moveEnemy() {
    	for(Enemy n: enemies) {
    		n.move(player.x, player.y);
    	}
    }

    public void pause(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException ex) {
            System.out.println("Error occurred!");
        }
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel(){
            this.setPreferredSize(new Dimension(500,500));
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            player.draw(g);
            mainMap.draw(g);
            enemy.draw(g);

            g.setColor(Color.BLACK);
            g.drawString("Materials Count: " + player.getInventory() , 10, 15);
            //drawmap(g, main_map);
        }
    }

    public void playAnimation() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        while (true) {
            mainMap.move(player);
            Toolkit.getDefaultToolkit().sync();
            repaint();
            pause(5);
        }
    }
}
