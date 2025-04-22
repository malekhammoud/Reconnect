import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class Enemy extends Rectangle {
	double v = 0.5, x, y;
	int width, height, counter;
	
	Enemy(double x, double y, int w, int h, double v) {
		this.x = x;
		this.y = y;
		this.v = v;
		this.width = w;
		this.height = h;
	}
	
	void draw(Graphics g) {
		g.setColor(Color.red);
		g.fillRect((int)this.x, (int)this.y, this.width, this.height);
	}
	
	public void move(double pX, double pY) {
		if(counter % 2 == 0) {
			if(pX > this.x) {this.x+= v;}
			if(pX < this.x) {this.x-= v;}
			if(pY > this.y) { this.y+= v;}
			if(pY < this.y) {this.y-= v;}
		}
		counter++;
			
		}
	}
