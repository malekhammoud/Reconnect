import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Player extends Rectangle implements ActionListener {
    double v;
    double[] vy = {0,0};
    double v_normal;
    double v_slow;
    double[] vx = {0,0};
    int width, height;
    double  x, y;
    boolean stop = false;

    ArrayList<Player> shadows =  new ArrayList<Player>();
    Timer timer = new Timer(10, this);
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
        timer.start();
        timer.addActionListener(this::actionPerformed);
        this.centerBoundary = centerBoundary;
        int border = 30;
        this.centerBoundarySm = new Rectangle(centerBoundary.x+border, centerBoundary.y+border, centerBoundary.width-border*2, centerBoundary.height-border*2);
        this.v_normal = v;
        this.v_slow = 0.1;
    }
    Player(int x, int y, int w, int h, double v) {
        this.v = v;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.c =new Color(0f,0f,0f,.03f );
    }
    void moveup(){
        this.vy[0] = -this.v;
    }
    void movedown(){
        this.vy[1] =this.v;
    }
    void moveleft(){
        this.vx[0] = -this.v;
    }
    void moveright(){
        this.vx[1] =this.v;
    }
    void drawSingle(Graphics g){
        g.setColor(this.c);
        g.fillRect((int)this.x,(int)this.y,this.width,this.height);
    }
    void draw(Graphics g){
        for(int i = 0; i<this.shadows.size(); i++){
            this.shadows.get(i).drawSingle(g);
        }
        //Draw the bounding logic rectangles:
        g.setColor(Color.GRAY);
        g.fillRect(this.centerBoundary.x,this.centerBoundary.y,this.centerBoundary.width,this.centerBoundary.height);

        g.setColor(Color.YELLOW);
        g.fillRect(this.centerBoundarySm.x,this.centerBoundarySm.y,this.centerBoundarySm.width,this.centerBoundarySm.height);
            g.setColor(Color.YELLOW);
            int border = 5;
            Rectangle top = new Rectangle(this.centerBoundary.x,this.centerBoundary.y-border,this.centerBoundary.width,border);
            Rectangle bottom= new Rectangle(this.centerBoundary.x,this.centerBoundary.y+this.centerBoundary.height,this.centerBoundary.width,border);
            Rectangle left= new Rectangle(this.centerBoundary.x-border,this.centerBoundary.y, border,this.centerBoundary.height);
            Rectangle right= new Rectangle(this.centerBoundary.x+this.centerBoundary.width,this.centerBoundary.y, border,this.centerBoundary.height);
            g.fillRect(top.x,top.y,top.width,top.height);
            g.fillRect(bottom.x,bottom.y,bottom.width,bottom.height);
            g.fillRect(left.x,left.y,left.width,left.height);
            g.fillRect(right.x,right.y,right.width,right.height);

        g.setColor(this.c);
        g.fillRect((int)this.x,(int)this.y,this.width,this.height);
    }
    void move(){
        if (this.centerBoundarySm.intersects(this.getrect())) {
            this.v = this.v_normal;
        }else if (this.centerBoundary.intersects(this.getrect())) {
            this.v = this.v_slow;
        }
        int border = 5;
        Rectangle top = new Rectangle(this.centerBoundary.x,this.centerBoundary.y-border,this.centerBoundary.width,border);
        Rectangle bottom= new Rectangle(this.centerBoundary.x,this.centerBoundary.y+this.centerBoundary.height,this.centerBoundary.width,border);
        Rectangle left= new Rectangle(this.centerBoundary.x-border,this.centerBoundary.y, border,this.centerBoundary.height);
        Rectangle right= new Rectangle(this.centerBoundary.x+this.centerBoundary.width,this.centerBoundary.y, border,this.centerBoundary.height);
        this.checkCollision(top);
        this.checkCollision(bottom);
        this.checkCollision(left);
        this.checkCollision(right);
        if(!this.stop){
            this.x += this.vx[0] + this.vx[1];
            this.y += this.vy[0] + this.vy[1];
        }
        if(this.vx[0] == 0 && this.vy[0] == 0 && this.vx[1] == 0 && this.vy[1] == 0){
            this.x = this.centerBoundary.x+this.centerBoundary.width/2-this.width/2;
            this.y = this.centerBoundary.y+this.centerBoundary.height/2-this.height/2;
        }
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
    boolean checkCollision(Rectangle r){
        if (this.getTop().intersects(r)) {
            this.vy[0]=0.000000001;
            this.y += this.v;
            return true;
        }
        if (this.getBottom().intersects(r)) {
            this.vy[1]=-0.000000001;
            this.y -= this.v;
            return true;
        }
        if (this.getLeft().intersects(r)) {
            this.vx[0]=-0.000000001;
            this.x += this.v;
            return true;
        }
        if (this.getRight().intersects(r)) {
            this.vx[1]=0.000000001;
            this.x -= this.v;
            return true;
        }
        return false;
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.shadows.add(new Player((int)this.x,(int) this.y, 20, 20, 0.000001));
        if(this.shadows.size()>25){
            this.shadows.remove(0);
        }
    }
}
