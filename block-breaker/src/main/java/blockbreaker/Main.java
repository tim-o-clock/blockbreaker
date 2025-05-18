package blockbreaker;

import javax.swing.JFrame;
import java.awt.EventQueue;

public class Main {
    public static void main(String[] args) {
        // Stellt sicher, dass Swing-Komponenten im Event Dispatch Thread erstellt werden
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Block Breaker");
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false); // Verhindert Größenänderung des Fensters!
            frame.pack(); // Passt die Fenstergröße an die preferredSize des GamePanels an
            frame.setLocationRelativeTo(null); // Zentriert das Fenster
            frame.setVisible(true);

            // Der Game-Loop wird jetzt im GamePanel gestartet, wenn der Benutzer ENTER drückt.
        });
    }
}