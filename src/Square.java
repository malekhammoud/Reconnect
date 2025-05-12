import java.awt.*;

public class Square extends Rectangle {
    int xNum, yNum;
    public Square(int x, int y, int width, int height, int xNum, int yNum) {
        super(x, y, width, height);
        this.xNum = xNum;
        this.yNum = yNum;
    }
}
