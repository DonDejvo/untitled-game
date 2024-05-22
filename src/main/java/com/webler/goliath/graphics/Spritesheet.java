package com.webler.goliath.graphics;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    @Getter
    private final Texture texture;
    private final List<Sprite> sprites;
    @Getter
    private final int spriteWidth;
    @Getter
    private final int spriteHeight;
    @Getter
    private final int numSprites;
    @Getter
    private final int spacing;
    @Getter
    private final int cols;
    @Getter
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

        // Creates a new Sprite and adds it to the list of sprites.
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

    /**
    * Gets the Sprite at the specified index. This is useful for debugging and to get information about the sprite that is in the sprite list
    * 
    * @param index - The index of the sprite to get
    * 
    * @return The Sprite at the specified index in the sprite list or null if there is no Sprite at
    */
    public Sprite getSprite(int index) {
        return new Sprite(this.sprites.get(index));
    }

}
