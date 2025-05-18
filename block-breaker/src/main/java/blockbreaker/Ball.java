package blockbreaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Ball extends GameObject {
    private int dx, dy; // Bewegungsrichtung und Geschwindigkeit
    private int radius;
    private GamePanel gamePanel; // Referenz auf das GamePanel für Kollisionen etc.

    public static final int INITIAL_SPEED = 3; // Anfangsgeschwindigkeit

    public Ball(int x, int y, int radius, GamePanel gamePanel) {
        super(x, y, radius * 2, radius * 2); // Breite und Höhe sind Durchmesser
        this.radius = radius;
        this.dx = INITIAL_SPEED; // Startrichtung
        this.dy = -INITIAL_SPEED; // Startrichtung
        this.gamePanel = gamePanel;
    }

    public void move() {
        x += dx;
        y += dy;

        // Kollision mit den Wänden des Spielfelds
        if (x <= 0) {
            dx = -dx; // Richtung umkehren
            x = 0; // Verhindern, dass der Ball außerhalb bleibt
        }
        if (x + width >= gamePanel.getWidth()) {
            dx = -dx;
            x = gamePanel.getWidth() - width;
        }
        if (y <= 0) {
            dy = -dy;
            y = 0;
        }
        // Kollision mit dem unteren Rand (Ball verloren) wird im GamePanel gehandhabt
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, width, height);
    }

    // Überschreiben von getBounds für eine präzisere Kreis-Kollision (optional, Rechteck ist einfacher)
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void reverseYDirection() {
        dy = -dy;
    }

    public void reverseXDirection() {
        dx = -dx;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void resetPosition(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        // Zufällige Startrichtung oder Standard
        this.dx = INITIAL_SPEED;
        this.dy = -INITIAL_SPEED;
    }
}