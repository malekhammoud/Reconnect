import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class Enemy extends Rectangle {
	int width, height;
	double v;
	
	Enemy(int x, int y, int w, int h, double v) {
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
		if(pX > x) {x++;}
		if(pX < x) {x--;}
		if(pY > y) {y++;}
		if(pY < y) {y--;}
			
		}
	}
