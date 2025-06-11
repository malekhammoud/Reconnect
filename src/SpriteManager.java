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
    private int currentState = 0;
    // current frame (0 or 1)
    private int currentFrame = 0;
    // sprite dimensions
    private int SPRITE_WIDTH;  // half of 1024
    private int SPRITE_HEIGHT; // quarter of 1280
    // timer for animation
    private long lastFrameTime = 0;
    // frame change interval (1 second)
    private final long FRAME_INTERVAL = 1000;

    public SpriteManager(int sw, int sh, String path) {
        SPRITE_WIDTH = sw;
        SPRITE_HEIGHT = sh;
        try {
            // Use the loadImage method from playerMotion class
            spriteSheet = playerMotion.loadImage(path);
            if (spriteSheet == null) {
                System.err.println("Failed to load sprite sheet: " + path);
            } else {
                // set initial sprite (down direction, first frame)
                updateCurrentSprite();
            }
        } catch (Exception e) {
            System.err.println("Error loading sprite sheet: " + e.getMessage());
        }
    }
        // update direction based on player movement
    public void setDirection(int dirX, int dirY) {
        // determine direction based on movement vector
        if (dirY > 0) currentState = 0;      // down
        else if (dirX < 0) currentState = 1; // left
        else if (dirY < 0) currentState = 2; // up
        else if (dirX > 0) currentState = 3; // right
        updateCurrentSprite();
    }
    public void updateState(int newState) {
        // determine direction based on movement vector
        currentState = newState;
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
    public void updateCurrentSprite() {
        if (spriteSheet != null) {
            // calculate position in sprite sheet based on direction and current frame
            int x = currentFrame * SPRITE_WIDTH;
            int y = currentState * SPRITE_HEIGHT;
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
