package com.webler.goliath.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import static org.lwjgl.stb.STBImage.*;

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

    public int getTexId() {
        return texId;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public boolean equals(Texture texture) {
        return texture.getTexId() == texId;
    }

    @Override
    public int hashCode() {
        return texId;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void destroy() {
        glDeleteTextures(texId);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texId);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void generateTextureId() {
        texId = glGenTextures();
    }

    public void loadFromImage(BufferedImage image) {
        int[] width = new int[1];
        int[] height = new int[1];
        int format;
        ByteBuffer data = convertImageData(image, width, height);

        generateTextureId();
        this.width = width[0];
        this.height = height[0];

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

    private ByteBuffer convertImageData(BufferedImage bufferedImage, int[] width, int[] height) {
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = bufferedImage.getWidth();
        int texHeight = bufferedImage.getHeight();

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
