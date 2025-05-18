package blockbreaker;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList; // Wird hier nicht direkt benötigt, aber oft in checkCollisions, falls man Blöcke sammeln würde
import java.util.List; // dito

public class GamePanel extends JPanel implements Runnable {

    // Konstanten für das Spielfeld
    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;
    private static final int PADDLE_START_Y = PANEL_HEIGHT - 70;
    private static final int BALL_START_Y_OFFSET = 30; // Offset über dem Paddle für den Ballstart

    // Spiel-Thread und Zustand
    private Thread gameThread;
    private boolean running = false;

    // Spielobjekte und Manager
    private Ball ball;
    private Paddle paddle;
    private LevelManager levelManager;
    private ScoreManager scoreManager;
    private SoundManager soundManager;

    // Grafiken
    private Image backgroundImage;

    // Spielzustände
    private enum GameState {
        START_SCREEN,
        PLAYING,
        GAME_OVER,
        LEVEL_WON,
        GAME_WON // Alle Level geschafft
    }
    private GameState gameState;

    // Konstruktor
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK); // Fallback, falls Hintergrundbild nicht lädt
        setFocusable(true);
        addKeyListener(new GameKeyAdapter());

        soundManager = new SoundManager(); // SoundManager zuerst initialisieren
        loadImages(); // Bilder laden
        initializeGameComponents(); // Spielkomponenten initialisieren
    }

    private void loadImages() {
        try {
            URL bgImageUrl = getClass().getResource("/images/background.JPG");
            if (bgImageUrl == null) {
                System.err.println("Hintergrundbild nicht gefunden: /images/background.png. Standardhintergrund wird verwendet.");
                backgroundImage = null;
            } else {
                backgroundImage = ImageIO.read(bgImageUrl);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden des Hintergrundbildes: " + e.getMessage());
            e.printStackTrace();
            backgroundImage = null;
        }
    }

    private void initializeGameComponents() {
        // Paddle wird zuerst erstellt, da Ball relativ dazu positioniert wird
        paddle = new Paddle((PANEL_WIDTH - Paddle.PADDLE_WIDTH) / 2, PADDLE_START_Y, this);
        ball = new Ball(
            paddle.getX() + (paddle.getWidth() / 2) - (Ball.INITIAL_RADIUS), // Ball.INITIAL_RADIUS ist hier Annahme
            paddle.getY() - Ball.INITIAL_RADIUS * 2 - 5, // 5 ist kleiner Puffer, Höhe des Balls ist 2*Radius
            Ball.INITIAL_RADIUS, // Annahme, dass Ball eine Konstante für den Radius hat
            this
        );
        scoreManager = new ScoreManager();
        levelManager = new LevelManager(this); // 'this' (GamePanel) wird für Breiteninfo übergeben

        gameState = GameState.START_SCREEN;
    }

    private void startGame() {
        scoreManager.resetScore();
        scoreManager.resetLives();
        levelManager.loadLevel(1); // Start mit Level 1
        resetPaddleAndBall();
        gameState = GameState.PLAYING;

        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();
        }
    }

    private void resetPaddleAndBall() {
        paddle.resetPosition((PANEL_WIDTH - paddle.getWidth()) / 2, PADDLE_START_Y);
        // Ball mittig auf dem Paddle positionieren, etwas darüber
        ball.resetPosition(
            paddle.getX() + (paddle.getWidth() / 2) - (ball.getWidth() / 2),
            paddle.getY() - ball.getHeight() - 5 // 5 Pixel Abstand über dem Paddle
        );
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Der Spiel-Thread wird jetzt durch eine Aktion im Startbildschirm gestartet (Enter)
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                if (gameState == GameState.PLAYING) {
                    updateGame();
                }
                delta--;
            }

            repaint();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                // System.out.println("FPS: " + frames); // Optional für Debugging
                frames = 0;
            }

            try {
                Thread.sleep(5); // Kleine Pause zur CPU-Entlastung
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt(); // Thread-Interrupt-Status wiederherstellen
            }
        }
    }

    private void updateGame() {
        ball.move();
        checkCollisions();

        if (levelManager.areAllBlocksDestroyed()) {
            if (levelManager.getCurrentLevel() < 2) { // Annahme: Es gibt 2 Level
                gameState = GameState.LEVEL_WON;
                soundManager.playSound(SoundManager.LEVEL_WON);
            } else {
                gameState = GameState.GAME_WON;
                soundManager.playSound(SoundManager.LEVEL_WON); // oder einen speziellen GAME_WON Sound
            }
        }

        // Ball unten raus
        if (ball.getY() + ball.getHeight() > PANEL_HEIGHT) {
            scoreManager.loseLife();
            soundManager.playSound(SoundManager.BALL_LOST);
            if (scoreManager.hasLives()) {
                resetPaddleAndBall();
            } else {
                gameState = GameState.GAME_OVER;
                soundManager.playSound(SoundManager.GAME_OVER);
            }
        }
    }

    private void checkCollisions() {
        // Kollision Ball mit Paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.reverseYDirection();
            soundManager.playSound(SoundManager.PADDLE_HIT);

            // Ballrichtung basierend auf Treffpunkt am Paddle anpassen
            int paddleCenterX = paddle.getX() + paddle.getWidth() / 2;
            int ballCenterX = ball.getX() + ball.getWidth() / 2;
            int difference = ballCenterX - paddleCenterX;
            // Ändert die x-Geschwindigkeit des Balls; der Divisor steuert die Empfindlichkeit
            ball.setDx(ball.getDx() + difference / (paddle.getWidth()/10)); // Divisor anpassen für gewünschtes Verhalten

            // Verhindern, dass Ball im Paddle stecken bleibt
            ball.setY(paddle.getY() - ball.getHeight());
        }

        // Kollision Ball mit Blöcken
        for (Block block : levelManager.getBlocks()) {
            if (block.isVisible() && ball.getBounds().intersects(block.getBounds())) {
                block.hit();
                scoreManager.addScore(10 * block.getStrength()); // Mehr Punkte für stärkere Blöcke TODO
                soundManager.playSound(SoundManager.BLOCK_HIT);

                // Kollisionslogik (einfache Umkehrung, kann verfeinert werden)
                // Um festzustellen, von welcher Seite der Ball kommt, braucht man mehr Logik.
                // Hier eine vereinfachte Annahme:
                Rectangle ballBounds = ball.getBounds();
                Rectangle blockBounds = block.getBounds();
                Rectangle intersection = ballBounds.intersection(blockBounds);

                if (intersection.width < intersection.height) { // Wahrscheinlich seitlicher Treffer
                    ball.reverseXDirection();
                     // Korrektur, um Steckenbleiben zu verhindern
                    if (ball.getDx() > 0) { // War auf dem Weg nach rechts
                        ball.setX(block.getX() - ball.getWidth());
                    } else { // War auf dem Weg nach links
                        ball.setX(block.getX() + block.getWidth());
                    }
                } else { // Wahrscheinlich Treffer von oben/unten
                    ball.reverseYDirection();
                    // Korrektur, um Steckenbleiben zu verhindern
                    if (ball.getDy() > 0) { // War auf dem Weg nach unten
                        ball.setY(block.getY() - ball.getHeight());
                    } else { // War auf dem Weg nach oben
                        ball.setY(block.getY() + block.getHeight());
                    }
                }
                break; // Nur ein Block pro Frame treffen
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Hintergrundbild oder Fallback-Farbe
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }

        // Zeichnen basierend auf dem Spielzustand
        switch (gameState) {
            case START_SCREEN:
                drawStartScreen(g2d);
                break;
            case PLAYING:
                paddle.draw(g2d);
                ball.draw(g2d);
                levelManager.drawBlocks(g2d);
                scoreManager.draw(g2d, PANEL_WIDTH); // Score und Leben
                // Level-Anzeige
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.drawString("Level: " + levelManager.getCurrentLevel(), PANEL_WIDTH / 2 - 30, 30);
                break;
            case GAME_OVER:
                drawGameOver(g2d);
                scoreManager.draw(g2d, PANEL_WIDTH); // Zeige finalen Score und Leben (0)
                break;
            case LEVEL_WON:
                drawLevelWon(g2d);
                scoreManager.draw(g2d, PANEL_WIDTH);
                break;
            case GAME_WON:
                drawGameWon(g2d);
                scoreManager.draw(g2d, PANEL_WIDTH);
                break;
        }
        Toolkit.getDefaultToolkit().sync(); // Für flüssigere Animationen auf manchen Systemen
    }

    private void drawStartScreen(Graphics2D g) {
        g.setColor(new Color(0,0,0,150)); // Leicht transparenter Hintergrund für Text
        g.fillRect(PANEL_WIDTH / 2 - 250, PANEL_HEIGHT / 2 - 100, 500, 200);

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "BLOCK BREAKER";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (PANEL_WIDTH - titleWidth) / 2, PANEL_HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String startMsg = "Press ENTER to Start";
        int startMsgWidth = g.getFontMetrics().stringWidth(startMsg);
        g.drawString(startMsg, (PANEL_WIDTH - startMsgWidth) / 2, PANEL_HEIGHT / 2 + 20);

        String controlMsg = "Use LEFT/RIGHT Arrow Keys to Move Paddle";
        int controlMsgWidth = g.getFontMetrics().stringWidth(controlMsg);
        g.drawString(controlMsg, (PANEL_WIDTH - controlMsgWidth) / 2, PANEL_HEIGHT / 2 + 50);

        String escMsg = "Press ESC to Quit (from Start Screen)";
        int escMsgWidth = g.getFontMetrics().stringWidth(escMsg);
        g.drawString(escMsg, (PANEL_WIDTH - escMsgWidth) / 2, PANEL_HEIGHT / 2 + 80);
    }

    private void drawGameOver(Graphics2D g) {
        drawMessageScreen(g, "GAME OVER", Color.RED, "Press ENTER to Restart");
    }

    private void drawLevelWon(Graphics2D g) {
        drawMessageScreen(g, "LEVEL " + levelManager.getCurrentLevel() + " CLEARED!", Color.ORANGE, "Press ENTER for Next Level");
    }

    private void drawGameWon(Graphics2D g) {
        drawMessageScreen(g, "CONGRATULATIONS! YOU WON!", Color.GREEN, "Press ENTER to Play Again");
    }

    private void drawMessageScreen(Graphics2D g, String title, Color titleColor, String subtitle) {
        g.setColor(new Color(0,0,0,180)); // Dunklerer transparenter Hintergrund
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(titleColor);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (PANEL_WIDTH - titleWidth) / 2, PANEL_HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (PANEL_WIDTH - subtitleWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }


    // Innere Klasse für Tastatureingaben
    private class GameKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (gameState == GameState.PLAYING) {
                if (key == KeyEvent.VK_LEFT) {
                    paddle.moveLeft();
                }
                if (key == KeyEvent.VK_RIGHT) {
                    paddle.moveRight();
                }
            }

            if (key == KeyEvent.VK_ENTER) {
                switch (gameState) {
                    case START_SCREEN:
                    case GAME_OVER:
                    case GAME_WON:
                        startGame();
                        break;
                    case LEVEL_WON:
                        int nextLevel = levelManager.getCurrentLevel() + 1;
                        if (nextLevel <= 2) { // Annahme: Max 2 Level, anpassen falls mehr
                            levelManager.loadLevel(nextLevel);
                            resetPaddleAndBall();
                            gameState = GameState.PLAYING;
                        } else {
                            gameState = GameState.GAME_WON; // Alle Level geschafft
                            soundManager.playSound(SoundManager.LEVEL_WON); // oder spezifischer Game-Win Sound
                        }
                        break;
                    default:
                        break;
                }
            }

            if (key == KeyEvent.VK_ESCAPE) {
                if (gameState == GameState.START_SCREEN) {
                    System.exit(0); // Spiel beenden
                } else if (gameState == GameState.PLAYING || gameState == GameState.GAME_OVER || gameState == GameState.LEVEL_WON || gameState == GameState.GAME_WON) {
                    // running = false; // Stoppt den Thread sauberer, wenn man zurück zum Start will
                    // gameThread = null;
                    gameState = GameState.START_SCREEN; // Zurück zum Startbildschirm
                    initializeGameComponents(); // Alles zurücksetzen, um sauber neu zu starten
                    repaint(); // Neu zeichnen, um Startbildschirm anzuzeigen
                }
            }
        }
    }
}