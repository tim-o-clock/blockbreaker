package blockbreaker;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private Map<String, Clip> soundClips;

    public static final String PADDLE_HIT = "paddle_hit";
    public static final String BLOCK_HIT = "block_hit";
    public static final String BALL_LOST = "ball_lost";
    public static final String LEVEL_WON = "level_won";
    public static final String GAME_OVER = "game_over";
    public static final String BLOCK_BREAKER = "block_breaker";


    public SoundManager() {
        soundClips = new HashMap<>();
        try {
            loadSound(PADDLE_HIT, "/sounds/paddle_hit.wav"); // Pfad relativ zum res-Ordner
            loadSound(BLOCK_HIT, "/sounds/block_hit.wav");
            loadSound(BALL_LOST, "/sounds/ball_lost.wav");
            loadSound(LEVEL_WON, "/sounds/level_won.wav");
            loadSound(GAME_OVER, "/sounds/game_over.wav");
            loadSound(BLOCK_BREAKER, "/sounds/Block-Breacker.wav");
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadSound(String name, String filePath) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        URL soundURL = getClass().getResource(filePath);
        if (soundURL == null) {
            throw new IOException("Sounddatei nicht gefunden: " + filePath);
        }
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        soundClips.put(name, clip);
    }

    public void playSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop(); // Stoppt den Sound, falls er noch läuft
            }
            clip.setFramePosition(0); // Setzt den Sound auf den Anfang zurück
            clip.start();
        } else {
            System.err.println("Sound nicht gefunden: " + name);
        }
    }

    // Optional: Methode zum Loopen von Musik (nicht für kurze Effekte)
    public void loopSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Optional: Methode zum Stoppen eines Sounds
    public void stopSound(String name) {
        Clip clip = soundClips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}