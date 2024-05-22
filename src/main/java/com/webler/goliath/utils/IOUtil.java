package com.webler.goliath.utils;

import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class IOUtil {

    private IOUtil() {
    }

    /**
    * Resize a ByteBuffer to a new capacity. This is used to ensure that the ByteBuffer is not shared between threads
    * 
    * @param buffer - The buffer to resize.
    * @param newCapacity - The new capacity of the buffer. Must be greater than or equal to 0.
    * 
    * @return A new ByteBuffer with the same capacity as the original and the contents of the original buffer flipped as needed
    */
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
    * Reads an InputStream into a ByteBuffer. The resource must be in the classpath and it must have a readable byte stream
    * 
    * @param resourceName - the name of the resource to read
    * @param bufferSize - the size of the ByteBuffer to be created
    * 
    * @return a ByteBuffer containing the contents of the resource or null if the resource could not be read or there was an error
    */
    public static ByteBuffer ioResourceToByteBuffer(String resourceName, int bufferSize) throws IOException {
        ByteBuffer buffer;

        InputStream source = IOUtil.class.getClassLoader().getResourceAsStream(resourceName);
        // If the source is null throws FileNotFoundException.
        if (source == null) {
            throw new FileNotFoundException(resourceName);
        }

        try (ReadableByteChannel rbc = Channels.newChannel(source)) {
            buffer = createByteBuffer(bufferSize);

            // Read from the buffer and resize the buffer if necessary.
            while (true) {
                int bytes = rbc.read(buffer);
                // If bytes is 1 break the loop.
                if (bytes == -1) {
                    break;
                }
                // Resize the buffer to the next available number of bytes.
                if (buffer.remaining() == 0) {
                    buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

}