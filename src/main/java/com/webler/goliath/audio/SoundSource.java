package com.webler.goliath.audio;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {
    private final int sourceId;

    public SoundSource(boolean loop) {
        this.sourceId = alGenSources();

        // Switches to loop mode.
        if(loop) {
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
        }
        setGain(1.0);
    }

    /**
    * Sets the buffer to use. This is a convenience method that calls #stop () before setting the buffer.
    * 
    * @param bufferId - the id of the buffer to use. If you want to use a non - default buffer use #setBuffer ( int
    */
    public void setBuffer(int bufferId) {
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }

    /**
    * Sets the gain of the sound. Gain is multiplied by the global gain ( in dB ) to avoid flickering
    * 
    * @param gain - the gain to set
    */
    public void setGain(double gain) {
        alSourcef(sourceId, AL_GAIN, (float)(gain * AudioManager.getGlobalGain()));
    }

    /**
    * Plays the source. This is equivalent to calling #alSourcePlay ( int ) with the sourceId
    */
    public void play() {
        alSourcePlay(sourceId);
    }

    /**
    * Returns true if the sound source is playing. This is equivalent to calling #getSource ( java. lang. String ) with AL_SOURCE_STATE AL_SOURCE_STATE and checking the result.
    * 
    * 
    * @return true if the sound source is playing false otherwise ( not supported by OpenAL yet ) or if the source does not exist
    */
    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    /**
    * Pause the source. This is a no - op if the source is not paused. See alSource
    */
    public void pause() {
        alSourcePause(sourceId);
    }

    /**
    * Stops the source. This is equivalent to calling alSourceStop ( sourceId ). Note that you can't stop a source that is in progress
    */
    public void stop() {
        alSourceStop(sourceId);
    }

    /**
    * Destroys the OpenAL connection. This is called when the program is no longer needed to run on the device
    */
    public void destroy() {
        stop();
        alDeleteSources(sourceId);
    }

}
