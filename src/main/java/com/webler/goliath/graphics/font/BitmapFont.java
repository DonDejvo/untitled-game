package com.webler.goliath.graphics.font;

import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;

public class BitmapFont {
    private Spritesheet spritesheet;
    private char[] charset;

    public BitmapFont(Spritesheet spritesheet, char[] charset) {
        this.spritesheet = spritesheet;
        this.charset = charset;
    }

    public Sprite getCharSprite(char c) {
        for (int i = 0; i < charset.length; i++) {
            if (charset[i] == c) {
                return spritesheet.getSprite(i);
            }
        }
        return null;
    }

    public char[] getCharset() {
        return charset;
    }

    public Spritesheet getSpritesheet() {
        return spritesheet;
    }
}
