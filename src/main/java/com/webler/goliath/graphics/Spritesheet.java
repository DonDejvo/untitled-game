package com.webler.goliath.graphics;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    private final Texture texture;
    private final List<Sprite> sprites;
    private final int spriteWidth;
    private final int spriteHeight;
    private final int numSprites;
    private final int spacing;
    private final int cols;
    private final int margin;

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int cols, int spacing, int margin) {
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.numSprites = numSprites;
        this.spacing = spacing;
        this.sprites = new ArrayList<>();
        this.texture = texture;
        this.cols = cols;
        this.margin = margin;

        for (int i = 0; i < numSprites; i++) {
            int x = margin + (spriteWidth + spacing) * (i % cols);
            int y = margin + (spriteHeight + spacing) * (i / cols);

            Sprite sprite = new Sprite(texture);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            sprite.setRegion(x, y, spriteWidth, spriteHeight);

            sprites.add(sprite);
        }
    }

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int cols) {
        this(texture, spriteWidth, spriteHeight, numSprites, cols, 0, 0);
    }

    public Spritesheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int cols, int spacing) {
        this(texture, spriteWidth, spriteHeight, numSprites, cols, spacing, 0);
    }

    public Sprite getSprite(int index) {
        return new Sprite(this.sprites.get(index));
    }

    public int getNumSprites() {
        return numSprites;
    }

    public int getCols() {
        return cols;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getMargin() {
        return margin;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public Texture getTexture() {
        return texture;
    }
}
