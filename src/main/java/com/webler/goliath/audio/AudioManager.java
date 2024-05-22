package com.webler.goliath.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.openal.ALC10.*;

public class AudioManager {
    private static AudioManager instance = null;
    private long device;
    private long context;
    private final List<SoundSource> sources;
    private List<SoundSource> tempSources;
    private double globalGain;

    /**
    * Returns the singleton instance of AudioManager. This is useful for unit testing. If you want to run tests on different instances you should use #getInstance () instead.
    * 
    * 
    * @return the singleton instance of AudioManager or null if none exists in the system ( which could happen if an error occurs
    */
    public static AudioManager getInstance() {
        // Create a new instance of AudioManager.
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private AudioManager() {
        sources = new ArrayList<>();
        tempSources = new ArrayList<>();
        globalGain = 1.0;
    }

    /**
    * Initializes the OpenAL library. This must be called before any other methods are called in order to avoid memory leaks
    */
    public static void init() {
        AudioManager manager = AudioManager.getInstance();
        manager.device = alcOpenDevice((ByteBuffer) null);
        // OpenAL device is not open.
        if (manager.device == 0) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities(manager.device);
        manager.context = alcCreateContext(manager.device, (IntBuffer) null);
        // Creates an OpenAL context.
        if(manager.context == 0) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(manager.context);
        AL.createCapabilities(alcCapabilities);
    }

    /**
    * Creates a SoundSource that will loop or unloop the sound. You can use this to play a sound at any time without losing it until you stop it.
    * 
    * @param loop - true to loop the sound false otherwise. Default is false.
    * 
    * @return The newly created instance of SoundSource for use in calls to #start () or #stop ()
    */
    public static SoundSource createSoundSource(boolean loop) {
        SoundSource soundSource = new SoundSource(loop);
        getInstance().sources.add(soundSource);
        return soundSource;
    }

    /**
    * Plays a SoundSource to the specified buffer. This is useful for debugging and to prevent accidental changes in the state of the sound system that are done in the main thread.
    * 
    * @param bufferId - The ID of the buffer to play.
    * @param gain - The gain to apply to the buffer in Hz
    */
    public static void play(int bufferId, double gain) {
        SoundSource soundSource = new SoundSource(false);
        soundSource.setGain(gain);
        soundSource.setBuffer(bufferId);
        soundSource.play();
        getInstance().tempSources.add(soundSource);
    }

    /**
    * Play a buffer. This is equivalent to pressing the key in the Play menu. If you are using this to play a buffer you need to call #play ( int ) first.
    * 
    * @param bufferId - The buffer to play. This can be any buffer
    */
    public static void play(int bufferId) {
        play(bufferId, 1.0);
    }

    /**
    * Clears the AudioManager and destroys all SoundSources. You should call this when you no longer need to play a new sound
    */
    public static void clear() {
        AudioManager manager = getInstance();
        for (SoundSource source : manager.sources) {
            source.destroy();
        }
        manager.sources.clear();
        for (SoundSource source : manager.tempSources) {
            source.destroy();
        }
        manager.tempSources.clear();
    }

    /**
    * Destroys the AudioManager and cleans up resources. This should be called when you no longer need the AudioManager
    */
    public static void destroy() {
        AudioManager manager = AudioManager.getInstance();
        clear();
        alcDestroyContext(manager.context);
        alcCloseDevice(manager.device);
    }

    /**
    * Ends the current frame. Destroys all sound sources that are no longer playing and removes them from the list
    */
    public static void endFrame() {
        AudioManager manager = getInstance();
        for (SoundSource source : manager.tempSources) {
            // Destroy the source object if playing.
            if (!source.isPlaying()) {
                source.destroy();
            }
        }
        manager.tempSources = manager.tempSources.stream().filter(SoundSource::isPlaying).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
    * Returns the Gain in millimeters. This is set to 0. 0 if not set.
    * 
    * 
    * @return The Gain in millimeters. This is set to 0. 0 if not set or if the value is less than or equal
    */
    public static double getGlobalGain() {
        return getInstance().globalGain;
    }

    /**
    * Sets the Gain for the global effect. This is used to apply a filter to the global effect when it is in effect.
    * 
    * @param globalGain - the Gain to apply to the global
    */
    public static void setGlobalGain(double globalGain) {
        getInstance().globalGain = globalGain;
    }

    /**
    * Returns the SoundSources that have been registered. This is useful for debugging and to avoid having to re - register every sound source in the application's main thread.
    * 
    * 
    * @return A list of sound sources that have been registered for debugging and can be used to rebuild the list of sounds
    */
    public static List<SoundSource> getSources() {
        return getInstance().sources;
    }
}
