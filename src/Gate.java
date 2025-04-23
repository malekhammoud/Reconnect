import java.awt.*;

public class Gate {
    Boolean working;
    Boolean open;
    int x;
    int y;
    int size;
    Color color;
    Gate(int x, int y, int size) {
        this.working = false;
        this.open = false;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = Color.blue;
    }

    void draw(Graphics g, double x, double y, int size) {
        if (this.working){
            this.color = Color.ORANGE;
            if (this.open){
                this.color = Color.green;
            }
        }else{
            this.color = Color.red;
        }
        g.setColor(color);
        g.fillRect((int)(this.x*size+x), (int)(this.y*size+y), this.size, this.size);
    }
    Rectangle getrect(double x, double y, int size) {
        return new Rectangle((int) (this.x*size+x), (int)(this.y*size+y), size,size);
    }
}
