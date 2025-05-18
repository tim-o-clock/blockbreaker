package blockbreaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private List<Block> blocks;
    private int currentLevel;
    private GamePanel gamePanel; // Für Breiten- und Höheninformationen (primär Breite hier)

    // Konstanten für Block-Layout
    public static final int BLOCK_WIDTH = 60;
    public static final int BLOCK_HEIGHT = 20;
    public static final int BLOCK_PADDING = 5; // Abstand zwischen Blöcken
    public static final int LEVEL_TOP_MARGIN = 50; // Abstand vom oberen Rand

    // Farben für die Zeilen
    private final Color[] rowColors = {
            new Color(220, 20, 60),  // Crimson
            new Color(255, 140, 0),  // DarkOrange
            new Color(255, 215, 0),  // Gold
            new Color(50, 205, 50),   // LimeGreen
            new Color(30, 144, 255),  // DodgerBlue
            new Color(138, 43, 226)  // BlueViolet
    };

    public LevelManager(GamePanel gamePanel) {
        this.blocks = new ArrayList<>();
        this.currentLevel = 0; // Wird in loadLevel gesetzt
        this.gamePanel = gamePanel;
    }

    public void loadLevel(int levelNumber) {
        this.currentLevel = levelNumber;
        blocks.clear(); // Alte Blöcke entfernen

        int blocksPerRow = (gamePanel.getWidth() - BLOCK_PADDING) / (BLOCK_WIDTH + BLOCK_PADDING);

        switch (levelNumber) {
            case 1:
                // Einfaches Layout für Level 1
                int numRowsLevel1 = 4;
                for (int row = 0; row < numRowsLevel1; row++) {
                    Color blockColor = rowColors[row % rowColors.length];
                    for (int col = 0; col < blocksPerRow; col++) {
                        int blockX = BLOCK_PADDING + col * (BLOCK_WIDTH + BLOCK_PADDING);
                        int blockY = LEVEL_TOP_MARGIN + row * (BLOCK_HEIGHT + BLOCK_PADDING);
                        blocks.add(new Block(blockX, blockY, BLOCK_WIDTH, BLOCK_HEIGHT, blockColor, 1));
                    }
                }
                break;
            case 2:
                // Etwas komplexeres Layout für Level 2
                int numRowsLevel2 = 6;
                for (int row = 0; row < numRowsLevel2; row++) {
                    Color blockColor = rowColors[row % rowColors.length];
                    int strength = (row < 2) ? 2 : 1; // Obere zwei Reihen sind stärker
                    for (int col = 0; col < blocksPerRow; col++) {
                        // Beispiel: Ein paar Lücken lassen oder Muster
                        if (col % (row + 1) != 0 || row <2) { // Einfaches Muster für Lücken
                             int blockX = BLOCK_PADDING + col * (BLOCK_WIDTH + BLOCK_PADDING);
                             int blockY = LEVEL_TOP_MARGIN + row * (BLOCK_HEIGHT + BLOCK_PADDING);
                             blocks.add(new Block(blockX, blockY, BLOCK_WIDTH, BLOCK_HEIGHT, blockColor, strength));
                        }
                    }
                }
                break;
            // Fügen Sie hier weitere case-Blöcke für zusätzliche Level hinzu
            default:
                System.out.println("Level " + levelNumber + " nicht definiert. Lade Level 1.");
                loadLevel(1); // Fallback
                break;
        }
    }

    public void drawBlocks(Graphics2D g) {
        for (Block block : blocks) {
            block.draw(g); // Ruft die draw-Methode jedes sichtbaren Blocks auf
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public boolean areAllBlocksDestroyed() {
        for (Block block : blocks) {
            if (block.isVisible()) {
                return false; // Mindestens ein Block ist noch sichtbar
            }
        }
        return true; // Alle Blöcke sind zerstört
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}