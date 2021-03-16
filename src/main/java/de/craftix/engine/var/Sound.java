package de.craftix.engine.var;

import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Sound {
    protected File file;
    protected Player player;
    protected Thread thread;

    public Sound(File file) { this.file = file; }

    public void play() {
        if (isPlaying()) return;
        thread = new Thread(() -> {
            try {
                InputStream stream = new FileInputStream(file);
                player = new Player(stream);
                player.play();
            }catch (Exception e) { e.printStackTrace(); }
        });
        thread.start();
    }

    public void stop() {
        if (!isPlaying()) return;
        try {
            player.close();
            player = null;
            thread.interrupt();
        }catch (Exception e) { e.printStackTrace(); }
    }

    public boolean isPlaying() { return player != null; }
}
