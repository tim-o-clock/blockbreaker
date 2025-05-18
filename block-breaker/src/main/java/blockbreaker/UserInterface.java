package blockbreaker;

//Für dieses Beispiel wird die UI-Logik direkt in GamePanel und ScoreManager gehandhabt.
//Eine dedizierte UserInterface-Klasse könnte so aussehen:

/*
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class UserInterface {
 private ScoreManager scoreManager;
 private LevelManager levelManager;

 public UserInterface(ScoreManager scoreManager, LevelManager levelManager) {
     this.scoreManager = scoreManager;
     this.levelManager = levelManager;
 }

 public void drawGameInfo(Graphics2D g, int panelWidth, int panelHeight) {
     // Zeichne Punktestand und Leben (könnte auch im ScoreManager sein)
     g.setColor(Color.WHITE);
     g.setFont(new Font("Arial", Font.BOLD, 20));
     g.drawString("Score: " + scoreManager.getScore(), 20, 30);
     g.drawString("Lives: " + scoreManager.getLives(), panelWidth - 100, 30);
     g.drawString("Level: " + levelManager.getCurrentLevel(), panelWidth / 2 - 30, 30);
 }

 public void drawGameOver(Graphics2D g, int panelWidth, int panelHeight) {
     g.setColor(Color.RED);
     g.setFont(new Font("Arial", Font.BOLD, 50));
     String msg = "GAME OVER";
     int msgWidth = g.getFontMetrics().stringWidth(msg);
     g.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 50);

     g.setFont(new Font("Arial", Font.PLAIN, 20));
     String restartMsg = "Press ENTER to Restart";
     int restartMsgWidth = g.getFontMetrics().stringWidth(restartMsg);
     g.drawString(restartMsg, (panelWidth - restartMsgWidth) / 2, panelHeight / 2);
 }

 public void drawGameWon(Graphics2D g, int panelWidth, int panelHeight) {
     g.setColor(Color.GREEN);
     g.setFont(new Font("Arial", Font.BOLD, 50));
     String msg = "YOU WON!";
     int msgWidth = g.getFontMetrics().stringWidth(msg);
     g.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 50);

     g.setFont(new Font("Arial", Font.PLAIN, 20));
     String nextLevelMsg = "Press ENTER for Next Level or ESC to Quit";
     int nextLevelMsgWidth = g.getFontMetrics().stringWidth(nextLevelMsg);
     g.drawString(nextLevelMsg, (panelWidth - nextLevelMsgWidth) / 2, panelHeight / 2);
 }

  public void drawStartScreen(Graphics2D g, int panelWidth, int panelHeight) {
     g.setColor(Color.CYAN);
     g.setFont(new Font("Arial", Font.BOLD, 40));
     String msg = "BLOCK BREAKER";
     int msgWidth = g.getFontMetrics().stringWidth(msg);
     g.drawString(msg, (panelWidth - msgWidth) / 2, panelHeight / 2 - 50);

     g.setFont(new Font("Arial", Font.PLAIN, 20));
     String startMsg = "Press ENTER to Start";
     int startMsgWidth = g.getFontMetrics().stringWidth(startMsg);
     g.drawString(startMsg, (panelWidth - startMsgWidth) / 2, panelHeight / 2);
 }
}
*/