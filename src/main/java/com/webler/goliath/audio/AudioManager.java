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
    private List<SoundSource> sources;
    private List<SoundSource> tempSources;
    private double globalGain;

    public static AudioManager getInstance() {
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

    public static void init() {
        AudioManager manager = AudioManager.getInstance();
        manager.device = alcOpenDevice((ByteBuffer) null);
        if (manager.device == 0) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities(manager.device);
        manager.context = alcCreateContext(manager.device, (IntBuffer) null);
        if(manager.context == 0) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(manager.context);
        AL.createCapabilities(alcCapabilities);
    }

    public static SoundSource createSoundSource(boolean loop) {
        SoundSource soundSource = new SoundSource(loop);
        getInstance().sources.add(soundSource);
        return soundSource;
    }

    public static void play(int bufferId, double gain) {
        SoundSource soundSource = new SoundSource(false);
        soundSource.setGain(gain);
        soundSource.setBuffer(bufferId);
        soundSource.play();
        getInstance().tempSources.add(soundSource);
    }

    public static void play(int bufferId) {
        play(bufferId, 1.0);
    }

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

    public static void destroy() {
        AudioManager manager = AudioManager.getInstance();
        clear();
        alcDestroyContext(manager.context);
        alcCloseDevice(manager.device);
    }

    public static void endFrame() {
        AudioManager manager = getInstance();
        for (SoundSource source : manager.tempSources) {
            if (!source.isPlaying()) {
                source.destroy();
            }
        }
        manager.tempSources = manager.tempSources.stream().filter(SoundSource::isPlaying).collect(Collectors.toCollection(ArrayList::new));
    }

    public static double getGlobalGain() {
        return getInstance().globalGain;
    }

    public static void setGlobalGain(double globalGain) {
        getInstance().globalGain = globalGain;
    }

    public static List<SoundSource> getSources() {
        return getInstance().sources;
    }
}
