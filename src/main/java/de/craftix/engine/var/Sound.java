package de.craftix.engine.var;

import de.craftix.engine.GameEngine;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;

public class Sound implements Serializable {
    private AudioInputStream stream;
    private Clip clip;

    public Sound(InputStream audioStream) {
        try {
            stream = AudioSystem.getAudioInputStream(new BufferedInputStream(audioStream));
            clip = AudioSystem.getClip();
            clip.open(stream);
        }catch (Exception e) {
            GameEngine.throwError(e);
        }
    }

    public void setProperty(FloatControl.Type property, float value) {
        if (!clip.isControlSupported(property))
            GameEngine.throwError(new IllegalArgumentException("Property not supported"));
        FloatControl control = (FloatControl) clip.getControl(property);
        control.setValue(value);
    }
    public float getProperty(FloatControl.Type property) {
        if (!clip.isControlSupported(property))
            GameEngine.throwError(new IllegalArgumentException("Property not supported"));
        FloatControl control = (FloatControl) clip.getControl(property);
        return control.getValue();
    }

    public void setVolume(float volume) {
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float value = Mathf.map(volume, 0, 100, control.getMinimum(), 0);
        control.setValue(value);
    }

    public void play() { clip.start(); }
    public void stop() { clip.stop(); }

    public Clip getClip() { return clip; }
    public AudioInputStream getStream() { return stream; }
    public float getVolume() {
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return Mathf.map(control.getValue(), control.getMinimum(), 0, 0, 100);
    }
}
