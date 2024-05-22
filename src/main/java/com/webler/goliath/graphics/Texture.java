package com.webler.goliath.graphics;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

@Getter
public class Texture {
    private static final ComponentColorModel glAlphaColorModel = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[] {8,8,8,8},
            true,
            false,
            ComponentColorModel.TRANSLUCENT,
            DataBuffer.TYPE_BYTE);

    private static final ComponentColorModel glColorModel = new ComponentColorModel(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            new int[] {8,8,8,0},
            false,
            false,
            ComponentColorModel.OPAQUE,
            DataBuffer.TYPE_BYTE);

    private int texId;
    private int width;
    private int height;

    public Texture() {
        texId = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height) {
        texId = glGenTextures();
        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, texId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    /**
    * Compares this object with another object. This is used to determine if two objects are equal or not.
    * 
    * @param obj - the object to compare to. May be null.
    * 
    * @return true if the objects are equal false otherwise. Note that false is always returned for objects that are not equal
    */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
    * Compares this texture with another. Two textures are equal if they have the same texture id. This is used to determine if a Texture is part of a RenderState that can be re - used in an OpenGL context.
    * 
    * @param texture - the texture to compare with this one.
    * 
    * @return true if the two textures are equal false otherwise. Note that false does not imply that the texture is a Texture
    */
    public boolean equals(Texture texture) {
        return texture.getTexId() == texId;
    }

    /**
    * Returns hash code for this object. This is used to compare two objects for equality. The hashcode returned by this method is equal to the texId of the texture that is associated with this object.
    * 
    * 
    * @return hash code for this object for use in hashing algorithms and data structures like java. util. HashTable
    */
    @Override
    public int hashCode() {
        return texId;
    }

    /**
    * Destroys the texture. This is called by OpenGL when the texture is no longer needed to be used
    */
    public void destroy() {
        glDeleteTextures(texId);
    }

    /**
    * Binds this texture to the OpenGL context. This is called by #create ( GL_TEXTURE_2D )
    */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texId);
    }

    /**
    * Unbinds the texture. This is called when the OpenGL context is no longer needed and should not be used
    */
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
    * Generates a texture id for use in OpenGL. Must be called on the EGL thread to avoid deadlocks
    */
    private void generateTextureId() {
        texId = glGenTextures();
    }

    /**
    * Loads a texture from a BufferedImage. This is useful for preparing an OpenGL texture to work with the contents of an image that is stored in a file or other storage system such as a disk.
    * 
    * @param image - The image to load the texture from. Must not be null
    */
    public void loadFromImage(BufferedImage image) {
        int[] width = new int[1];
        int[] height = new int[1];
        int format;
        ByteBuffer data = convertImageData(image, width, height);

        generateTextureId();
        this.width = width[0];
        this.height = height[0];

        // Set the format of the image.
        if(image.getColorModel().hasAlpha()) {
            format = GL_RGBA;
        } else {
            format = GL_RGB;
        }

        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, format, width[0],height[0], 0, format, GL_UNSIGNED_BYTE, data);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        unbind();
    }

    /**
    * Converts the image data to a direct buffer. This is used to create an image that can be passed to #readTexImage ( java. awt. Image ) and then readTexImage ( java. awt
    * 
    * @param bufferedImage
    * @param width
    * @param height
    */
    private ByteBuffer convertImageData(BufferedImage bufferedImage, int[] width, int[] height) {
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = bufferedImage.getWidth();
        int texHeight = bufferedImage.getHeight();

        // Creates a buffered image with the color model.
        if(bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable<>());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable<>());
        }

        Graphics g = texImage.getGraphics();
        g.drawImage(bufferedImage, 0, 0, null);

        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        width[0] = texWidth;
        height[0] = texHeight;

        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }
}
