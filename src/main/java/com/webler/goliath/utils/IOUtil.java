package com.webler.goliath.utils;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class IOUtil {

    private IOUtil() {
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resourceName, int bufferSize) throws IOException {
        ByteBuffer buffer;

        InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resourceName);
        if (source == null) {
            throw new FileNotFoundException(resourceName);
        }

        try (ReadableByteChannel rbc = Channels.newChannel(source)) {
            buffer = createByteBuffer(bufferSize);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

}