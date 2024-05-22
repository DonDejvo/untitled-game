package com.webler.goliath.audio;

import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.webler.goliath.utils.IOUtil.ioResourceToByteBuffer;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.stb.STBVorbis.*;

@Getter
public class Sound {
    private int bufferId;

    public Sound() {
        bufferId = -1;
    }

    /**
    * Loads Vorbis data into OpenAL. This is a convenience method for #readVorbis ( String shortBuffer )
    * 
    * @param resourceName - Name of resource to
    */
    public void load(String resourceName) {
        bufferId = alGenBuffers();

        try(STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(resourceName, 32 * 1024, info);

            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }
    }

    /**
    * Reads and returns PCM data from vorbis file. This is a helper method for #read ( String ) that allows to pass in information about the stream to stb_vorbis_open_memory () and #stb_vorbis_get_samples_short_interleaved ().
    * 
    * @param resourceName - name of the resource to read from.
    * @param bufferSize - size of the buffer to use. Must be greater than 0.
    * @param info - STBVorbisInfo to fill with information.
    * 
    * @return ShortBuffer containing the audio data read from the vorbis file. It is big enough to hold 16 - bit values
    */
    private static ShortBuffer readVorbis(String resourceName, int bufferSize, STBVorbisInfo info) {
        ByteBuffer vorbis;
        try {
            vorbis = ioResourceToByteBuffer(resourceName, bufferSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IntBuffer error   = BufferUtils.createIntBuffer(1);
        long      decoder = stb_vorbis_open_memory(vorbis, error, null);
        // Open Ogg Vorbis file. If decoder is 0 throw RuntimeException.
        if (decoder == 0) {
            throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
        }

        stb_vorbis_get_info(decoder, info);

        int channels = info.channels();

        ShortBuffer pcm = BufferUtils.createShortBuffer(stb_vorbis_stream_length_in_samples(decoder) * channels);

        stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm);
        stb_vorbis_close(decoder);

        return pcm;
    }

    /**
    * Destroys the OpenAL buffer. This is a no - op if there is no OpenAL buffer
    */
    public void destroy() {
        alDeleteBuffers(bufferId);
    }
}
