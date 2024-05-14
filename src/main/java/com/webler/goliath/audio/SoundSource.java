package com.webler.goliath.audio;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {
    private int sourceId;

    public SoundSource(boolean loop) {
        this.sourceId = alGenSources();

        if(loop) {
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
        }
        setGain(1.0);
    }

    public void setBuffer(int bufferId) {
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }

    public void setGain(double gain) {
        alSourcef(sourceId, AL_GAIN, (float)(gain * AudioManager.getGlobalGain()));
    }

    public void play() {
        alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void destroy() {
        stop();
        alDeleteSources(sourceId);
    }

}
