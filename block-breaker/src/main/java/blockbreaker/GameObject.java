package blockbreaker;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class GameObject {
    protected int x, y; // Position
    protected int width, height; // Dimensionen

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Abstrakte Methode zum Zeichnen des Objekts
    public abstract void draw(Graphics2D g);

    // Methode zur Erstellung eines Rechtecks für Kollisionserkennung
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getter und Setter (optional, je nach Bedarf)
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}