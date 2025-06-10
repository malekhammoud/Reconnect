import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class SpriteManager {
    // sprite sheet
    private BufferedImage spriteSheet;
    // current sprite to display
    private BufferedImage currentSprite;
    // current direction (0=down, 1=left, 2=up, 3=right)
    private int currentDirection = 0;
    // current frame (0 or 1)
    private int currentFrame = 0;
    // sprite dimensions
    private final int SPRITE_WIDTH = 512;  // half of 1024
    private final int SPRITE_HEIGHT = 320; // quarter of 1280
    // timer for animation
    private long lastFrameTime = 0;
    // frame change interval (1 second)
    private final long FRAME_INTERVAL = 1000;

    public SpriteManager() {
        try {
            // load sprite sheet
            spriteSheet = ImageIO.read(new File("src/resources/sprites/spritesheetBob.png"));
            // set initial sprite (down direction, first frame)
            updateCurrentSprite();
        } catch (IOException e) {
            System.err.println("Error loading sprite sheet: " + e.getMessage());
        }
    }

    // update direction based on player movement
    public void setDirection(int dirX, int dirY) {
        // determine direction based on movement vector
        if (dirY > 0) currentDirection = 0;      // down
        else if (dirX < 0) currentDirection = 1; // left
        else if (dirY < 0) currentDirection = 2; // up
        else if (dirX > 0) currentDirection = 3; // right
        updateCurrentSprite();
    }

    // update animation frame
    public void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_INTERVAL) {
            // toggle between frames 0 and 1
            currentFrame = 1 - currentFrame;
            lastFrameTime = currentTime;
            updateCurrentSprite();
        }
    }

    // get the current sprite to render
    private void updateCurrentSprite() {
        if (spriteSheet != null) {
            // calculate position in sprite sheet based on direction and current frame
            int x = currentFrame * SPRITE_WIDTH;
            int y = currentDirection * SPRITE_HEIGHT;
            currentSprite = spriteSheet.getSubimage(x, y, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }

    // draw the sprite
    public void drawSprite(Graphics g, int x, int y, int width, int height) {
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        }
    }

    // get the current sprite image
    public BufferedImage getCurrentSprite() {
        return currentSprite;
    }
}
