package blockbreaker;

import java.awt.Color;
import java.awt.Graphics2D;

public class Block extends GameObject {
    private boolean visible;
    private Color color;
    private int strength; // Wie oft muss der Block getroffen werden

    public Block(int x, int y, int width, int height, Color color, int strength) {
        super(x, y, width, height);
        this.color = color;
        this.strength = strength;
        this.visible = true;
    }

    @Override
    public void draw(Graphics2D g) {
        if (visible) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK); // Rand für bessere Sichtbarkeit
            g.drawRect(x, y, width, height);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // Wird aufgerufen, wenn der Block vom Ball getroffen wird
    public void hit() {
        strength--;
        if (strength <= 0) {
            visible = false;
        }
        // Optional: Farbe ändern bei Treffer
        // if (strength == 1) this.color = Color.ORANGE;
    }

    public int getStrength() {
        return strength;
    }
}