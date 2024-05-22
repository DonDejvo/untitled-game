package com.webler.goliath.graphics;

import com.webler.goliath.utils.AssetPool;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Sprite {
    private Texture texture;
    private float[] texCoords;
    private int width, height;

    public Sprite(Texture texture) {
        this.width = texture.getWidth();
        this.height = texture.getHeight();
        this.texture = texture;
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
        this.texture = sprite.texture;
        this.texCoords = sprite.texCoords.clone();
        this.width = sprite.width;
        this.height = sprite.height;
    }

    /**
    * Sets the region of this texture. This is useful for drawing textures that are part of a larger texture than the texture used to draw the texture.
    * 
    * @param x - The x coordinate of the region to set.
    * @param y - The y coordinate of the region to set.
    * @param width - The width of the region to set. Must be greater than 0.
    * @param height - The height of the region to set. Must be greater than 0
    */
    public void setRegion(int x, int y, int width, int height) {
        int texWidth = texture.getWidth();
        int texHeight = texture.getHeight();
        texCoords[0] = (float)(x + 0.5) / texWidth;
        texCoords[1] = (float)(y + 0.5) / texHeight;
        texCoords[2] = (float)(x + width) / texWidth;
        texCoords[3] = (float)(y + 0.5) / texHeight;
        texCoords[4] = (float)(x + width) / texWidth;
        texCoords[5] = (float)(y + height) / texHeight;
        texCoords[6] = (float)(x + 0.5) / texWidth;
        texCoords[7] = (float)(y + height) / texHeight;
    }

}
