package blockbreaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class Paddle extends GameObject {
    private int speed;
    private GamePanel gamePanel; // Für Begrenzungen

    public static final int PADDLE_WIDTH = 100;
    public static final int PADDLE_HEIGHT = 15;
    public static final int PADDLE_SPEED = 15;

    public Paddle(int x, int y, GamePanel gamePanel) {
        super(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
        this.speed = PADDLE_SPEED;
        this.gamePanel = gamePanel;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) {
            x = 0; // Begrenzung links
        }
    }

    public void moveRight() {
        x += speed;
        if (x + width > gamePanel.getWidth()) {
            x = gamePanel.getWidth() - width; // Begrenzung rechts
        }
    }

    public void resetPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
}