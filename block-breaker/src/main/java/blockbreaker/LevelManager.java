package blockbreaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {
    private List<Block> blocks;
    private int currentLevel;
    private GamePanel gamePanel; // Für Breiten- und Höheninformationen!

    // Konstanten für Block-Layout
    public static final int BLOCK_WIDTH = 60;
    public static final int BLOCK_HEIGHT = 20;
    public static final int BLOCK_PADDING = 5; // Abstand zwischen Blöcken
    public static final int LEVEL_TOP_MARGIN = 50; // Abstand vom oberen Rand

    public LevelManager(GamePanel gamePanel) {
        this.blocks = new ArrayList<>();
        this.currentLevel = 1;
        this.gamePanel = gamePanel;
    }

    public void loadLevel(int levelNumber) {
        this.currentLevel = levelNumber;
        blocks.clear(); // Alte Blöcke entfernen

        // Beispiel-Level-Layouts
        if (levelNumber == 1) {
            // Einfaches Layout für Level 1
            int blocksPerRow = (gamePanel.getWidth() - BLOCK_PADDING) / (BLOCK_WIDTH + BLOCK_PADDING);
            int numRows = 4;
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < blocksPerRow; col++) {
                    int blockX = BLOCK_PADDING + col * (BLOCK_WIDTH + BLOCK_PADDING);
                    int blockY = LEVEL_TOP_MARGIN + row * (BLOCK_HEIGHT + BLOCK_PADDING);
                    blocks.add(new Block(blockX, blockY, BLOCK_WIDTH, BLOCK_HEIGHT, Color.GREEN, 1));
                }
            }
        } else if (levelNumber == 2) {
            // Etwas komplexeres Layout für Level 2
            int blocksPerRow = (gamePanel.getWidth() - BLOCK_PADDING) / (BLOCK_WIDTH + BLOCK_PADDING);
            int numRows = 5;
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < blocksPerRow; col++) {
                    if ((row + col) % 2 == 0) { // Schachbrettmuster
                        int blockX = BLOCK_PADDING + col * (BLOCK_WIDTH + BLOCK_PADDING);
                        int blockY = LEVEL_TOP_MARGIN + row * (BLOCK_HEIGHT + BLOCK_PADDING);
                        Color color = (row < 2) ? Color.RED : Color.CYAN;
                        int strength = (row < 2) ? 2 : 1;
                        blocks.add(new Block(blockX, blockY, BLOCK_WIDTH, BLOCK_HEIGHT, color, strength));
                    }
                }
            }
        }
        // Fügen Sie hier weitere Level hinzu
    }

    public void drawBlocks(Graphics2D g) {
        for (Block block : blocks) {
            block.draw(g);
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public boolean areAllBlocksDestroyed() {
        for (Block block : blocks) {
            if (block.isVisible()) {
                return false;
            }
        }
        return true;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}