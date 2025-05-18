package blockbreaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class ScoreManager {
    private int score;
    private int lives;
    public static final int INITIAL_LIVES = 3;

    public ScoreManager() {
        this.score = 0;
        this.lives = INITIAL_LIVES;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        this.lives--;
    }

    public void resetLives() {
        this.lives = INITIAL_LIVES;
    }

    public boolean hasLives() {
        return lives > 0;
    }

    public void draw(Graphics2D g, int panelWidth) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("Lives: " + lives, panelWidth - 100, 30);
    }
}