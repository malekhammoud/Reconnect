import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Player extends Rectangle{
    double v;
    int width, height;
    double  x, y;
    int inventory;

    ArrayList<Player> shadows =  new ArrayList<Player>();
    //Timer timer = new Timer(10, this);
    Rectangle centerBoundary;
    Rectangle centerBoundarySm;
    Color c;
    Player(double x, double y, int w, int h, double v, Color c, Rectangle centerBoundary) {
        this.v = v;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.c = c;
        this.inventory = 0;
       // timer.start();
        //timer.addActionListener(this::actionPerformed);
    }
    Player(int x, int y, int w, int h, double v) {
        this.v = v;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.c = new Color(0f,0f,0f,.03f );
    }
    void addInventory(){
        this.inventory++;
    }
    void removeInventory(){
        this.inventory--;
    }
    int getInventory(){
        return this.inventory;
    }

    void drawSingle(Graphics g){
        g.setColor(this.c);
        g.fillRect((int)this.x,(int)this.y,this.width,this.height);
    }
    void draw(Graphics g){
        for(int i = 0; i<this.shadows.size(); i++){
            this.shadows.get(i).drawSingle(g);
        }
        g.setColor(this.c);
        g.fillRect((int)this.x,(int)this.y,this.width,this.height);
    }
    Rectangle getTop(){
        return new Rectangle((int)this.x+this.width/5,(int)this.y,this.width-this.width/5 *2,this.height/2);
    }
    Rectangle getBottom(){
        return new Rectangle((int)this.x+this.width/5,(int)this.y+this.height/2,this.width-this.width/5*2,this.height/2);
    }
    Rectangle getRight(){
        return new Rectangle((int)this.x+this.width/2,(int)this.y+this.height/5,this.width/2,this.height-this.height/5 *2 );
    }
    Rectangle getLeft(){
        return new Rectangle((int)this.x,(int)this.y+this.height/5,this.width/2,this.height-this.height/5 *2 );
    }
    Rectangle getrect(){
        return new Rectangle((int)this.x,(int)this.y,this.width,this.height);
    }

    //Removing the blur feature, will add it later if have time
    /*
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.shadows.add(new Player((int)this.x,(int) this.y, 20, 20, 0.000001));
        if(this.shadows.size()>25){
            this.shadows.remove(0);
        }
    }
     */
}
