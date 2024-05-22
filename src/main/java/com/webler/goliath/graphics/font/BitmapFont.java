package com.webler.goliath.graphics.font;

import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;

public record BitmapFont(Spritesheet spritesheet, char[] charset) {

    /**
    * Returns the Sprite that corresponds to the char. This is used to determine if a character is part of a sprite or not
    * 
    * @param c - The character to look for
    * 
    * @return The Sprite or null if not found in the spritesheet or the character is not part of
    */
    public Sprite getCharSprite(char c) {
        // Returns the sprite that is currently in the charset.
        for (int i = 0; i < charset.length; i++) {
            // Returns the sprite at the given index.
            if (charset[i] == c) {
                return spritesheet.getSprite(i);
            }
        }
        return null;
    }

}
