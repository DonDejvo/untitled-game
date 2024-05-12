package com.webler.goliath.graphics;

import com.webler.goliath.utils.AssetPool;

public class Sprite {
    private Texture tex;
    private float[] texCoords;
    private int width, height;

    public Sprite(Texture tex) {
        this.width = tex.getWidth();
        this.height = tex.getHeight();
        this.tex = tex;
        texCoords = new float[] {
                0, 0,
                1, 0,
                1, 1,
                0, 1
        };
    }

    public Sprite() {
        this(AssetPool.getTexture("goliath/images/square.png"));
        width = 1;
        height = 1;
    }

    public Sprite(Sprite sprite) {
        this.tex = sprite.tex;
        this.texCoords = sprite.texCoords.clone();
        this.width = sprite.width;
        this.height = sprite.height;
    }

    public void setRegion(int x, int y, int width, int height) {
        int texWidth = tex.getWidth();
        int texHeight = tex.getHeight();
        texCoords[0] = (float)(x + 0.5) / texWidth;
        texCoords[1] = (float)(y + 0.5) / texHeight;
        texCoords[2] = (float)(x + width) / texWidth;
        texCoords[3] = (float)(y + 0.5) / texHeight;
        texCoords[4] = (float)(x + width) / texWidth;
        texCoords[5] = (float)(y + height) / texHeight;
        texCoords[6] = (float)(x + 0.5) / texWidth;
        texCoords[7] = (float)(y + height) / texHeight;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(float[] texCoords) {
        this.texCoords = texCoords;
    }

    public Texture getTexture() {
        return tex;
    }

    public void setTexture(Texture tex) {
        this.tex = tex;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
