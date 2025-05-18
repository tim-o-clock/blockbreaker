package blockbreaker;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    public static final int PANEL_WIDTH = 800;
    public static final int PANEL_HEIGHT = 600;
    private static final int PADDLE_START_Y = PANEL_HEIGHT - 70;
    private static final int BALL_START_Y_OFFSET = 30; // Offset über dem Paddle

    private Thread gameThread;
    private boolean running = false;

    private Ball ball;
    private Paddle paddle;
    private LevelManager levelManager;
    private ScoreManager scoreManager;
    // private UserInterface userInterface; // Wenn Sie eine separate UI-Klasse verwenden

    private enum GameState {
        START_SCREEN,
        PLAYING,
        GAME_OVER,
        LEVEL_WON,
        GAME_WON // Alle Level geschafft
    }
    private GameState gameState;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true); // Wichtig, um KeyEvents zu empfangen
        addKeyListener(new GameKeyAdapter());

        initializeGameComponents();
    }

    private void initializeGameComponents() {
        paddle = new Paddle((PANEL_WIDTH - Paddle.PADDLE_WIDTH) / 2, PADDLE_START_Y, this);
        ball = new Ball(
            paddle.getX() + (Paddle.PADDLE_WIDTH / 2) - 7, // 7 ist ungefähr der halbe Ballradius
            paddle.getY() - BALL_START_Y_OFFSET,
            10, // Ballradius
            this
        );
        scoreManager = new ScoreManager();
        levelManager = new LevelManager(this);
        // userInterface = new UserInterface(scoreManager, levelManager); // Bei separater UI-Klasse

        gameState = GameState.START_SCREEN; // Start mit dem Startbildschirm
    }

    private void startGame() {
        scoreManager.resetScore();
        scoreManager.resetLives();
        levelManager.loadLevel(1);
        resetPaddleAndBall();
        gameState = GameState.PLAYING;
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            gameThread.start();
            running = true;
        }
    }

    private void resetPaddleAndBall() {
        paddle.resetPosition((PANEL_WIDTH - Paddle.PADDLE_WIDTH) / 2, PADDLE_START_Y);
        ball.resetPosition(
            paddle.getX() + (Paddle.PADDLE_WIDTH / 2) - (ball.getWidth() / 2),
            paddle.getY() - ball.getHeight() - 5 // Kleiner Abstand über dem Paddle
        );
    }


    public void addNotify() {
        super.addNotify();
        // Startet den Game-Thread, wenn das Panel zum JFrame hinzugefügt wird,
        // aber nur, wenn er nicht bereits läuft und wir nicht im StartScreen sind.
        // Der eigentliche Start des Spiels (und des Threads) erfolgt jetzt durch Tastendruck.
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

            repaint(); // Zeichnet das Spiel neu
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames); // Für Debugging
                frames = 0;
            }

            // Kurze Pause, um die CPU nicht zu überlasten
            try {
                Thread.sleep(10); // Kann angepasst werden
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGame() {
        ball.move();
        checkCollisions();

        if (levelManager.areAllBlocksDestroyed()) {
            if (levelManager.getCurrentLevel() < 2) { // Annahme: 2 Level
                gameState = GameState.LEVEL_WON;
            } else {
                gameState = GameState.GAME_WON;
            }
            // running = false; // Oder Pausieren und auf Eingabe für nächstes Level warten
        }

        if (ball.getY() + ball.getHeight() >= PANEL_HEIGHT) { // Ball unten raus
            scoreManager.loseLife();
            if (scoreManager.hasLives()) {
                resetPaddleAndBall();
            } else {
                gameState = GameState.GAME_OVER;
                // running = false;
            }
        }
    }

    private void checkCollisions() {
        // Ball mit Paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            ball.reverseYDirection();
            // Optional: Ballrichtung basierend auf Treffpunkt am Paddle ändern
            int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            int ballCenter = ball.getX() + ball.getWidth() / 2;
            int difference = ballCenter - paddleCenter;
            ball.setDx(ball.getDx() + difference / 10); // Experimentieren Sie mit dem Divisor
            // Sicherstellen, dass der Ball nicht im Paddle stecken bleibt
            ball.setY(paddle.getY() - ball.getHeight());
        }

        // Ball mit Blöcken
        List<Block> blocksToRemove = new ArrayList<>();
        for (Block block : levelManager.getBlocks()) {
            if (block.isVisible() && ball.getBounds().intersects(block.getBounds())) {
                block.hit();
                scoreManager.addScore(10); // Punkte für getroffenen Block
                ball.reverseYDirection(); // Einfache Umkehrung, kann verfeinert werden

                if (!block.isVisible()) {
                    // Block ist zerstört
                }
                break; // Nur ein Block pro Frame treffen (vereinfacht)
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing für weichere Kanten (optional)
        // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameState == GameState.START_SCREEN) {
            drawStartScreen(g2d);
        } else if (gameState == GameState.PLAYING) {
            paddle.draw(g2d);
            ball.draw(g2d);
            levelManager.drawBlocks(g2d);
            scoreManager.draw(g2d, PANEL_WIDTH); // UI-Elemente
            // userInterface.drawGameInfo(g2d, PANEL_WIDTH, PANEL_HEIGHT); // Bei separater UI
        } else if (gameState == GameState.GAME_OVER) {
            drawGameOver(g2d);
            scoreManager.draw(g2d, PANEL_WIDTH); // Zeige finalen Score etc.
        } else if (gameState == GameState.LEVEL_WON) {
            drawLevelWon(g2d);
            scoreManager.draw(g2d, PANEL_WIDTH);
        } else if (gameState == GameState.GAME_WON) {
            drawGameWon(g2d);
            scoreManager.draw(g2d, PANEL_WIDTH);
        }

        Toolkit.getDefaultToolkit().sync(); // Hilft bei flüssigerer Animation auf manchen Systemen
    }

    private void drawStartScreen(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String msg = "BLOCK BREAKER";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (PANEL_WIDTH - msgWidth) / 2, PANEL_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String startMsg = "Press ENTER to Start";
        int startMsgWidth = g.getFontMetrics().stringWidth(startMsg);
        g.drawString(startMsg, (PANEL_WIDTH - startMsgWidth) / 2, PANEL_HEIGHT / 2 + 20);
        String controlMsg = "Use LEFT/RIGHT Arrow Keys to Move Paddle";
         int controlMsgWidth = g.getFontMetrics().stringWidth(controlMsg);
        g.drawString(controlMsg, (PANEL_WIDTH - controlMsgWidth) / 2, PANEL_HEIGHT / 2 + 50);
    }


    private void drawGameOver(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String msg = "GAME OVER";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (PANEL_WIDTH - msgWidth) / 2, PANEL_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String restartMsg = "Press ENTER to Restart";
        int restartMsgWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (PANEL_WIDTH - restartMsgWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }

    private void drawLevelWon(Graphics2D g) {
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String msg = "LEVEL " + levelManager.getCurrentLevel() + " CLEARED!";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (PANEL_WIDTH - msgWidth) / 2, PANEL_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String nextLevelMsg = "Press ENTER for Next Level";
        int nextLevelMsgWidth = g.getFontMetrics().stringWidth(nextLevelMsg);
        g.drawString(nextLevelMsg, (PANEL_WIDTH - nextLevelMsgWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }
     private void drawGameWon(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String msg = "YOU WON!";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (PANEL_WIDTH - msgWidth) / 2, PANEL_HEIGHT / 2 - 50);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String restartMsg = "Press ENTER to Play Again";
        int restartMsgWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (PANEL_WIDTH - restartMsgWidth) / 2, PANEL_HEIGHT / 2 + 20);
    }


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
                if (gameState == GameState.START_SCREEN || gameState == GameState.GAME_OVER || gameState == GameState.GAME_WON) {
                    startGame(); // Startet oder startet das Spiel neu
                } else if (gameState == GameState.LEVEL_WON) {
                    // Nächstes Level laden
                    int nextLevel = levelManager.getCurrentLevel() + 1;
                    // Hier sollten Sie prüfen, ob es ein nächstes Level gibt
                    if (nextLevel <= 2) { // Annahme: 2 Level
                        levelManager.loadLevel(nextLevel);
                        resetPaddleAndBall();
                        gameState = GameState.PLAYING;
                    } else {
                        gameState = GameState.GAME_WON; // Alle Level geschafft
                    }
                }
            }

            if (key == KeyEvent.VK_ESCAPE) {
                 if (gameState != GameState.START_SCREEN) { // Erlaube ESC, um zum Startbildschirm zurückzukehren
                    gameState = GameState.START_SCREEN;
                    running = false; // Stoppt den Game-Loop, wenn nicht im Startbildschirm
                    // initializeGameComponents(); // Setzt alles zurück
                    // repaint(); // Um den Startbildschirm sofort anzuzeigen
                } else {
                     System.exit(0); // Beendet das Spiel vom Startbildschirm aus
                 }
            }
        }
    }
}