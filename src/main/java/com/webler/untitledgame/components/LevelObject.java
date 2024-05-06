package com.webler.untitledgame.components;

import com.webler.goliath.graphics.Sprite;
import org.joml.Vector2d;

public abstract class LevelObject {
    private LevelObjectType type;
    private String identifier;
    private String name;
    private Sprite sprite;
    private Vector2d scale;
    private int zIndex;

    public LevelObject(LevelObjectType type, String identifier, String name, Sprite sprite, Vector2d scale, int zIndex) {
        this.type = type;
        this.identifier = identifier;
        this.name = name;
        this.sprite = sprite;
        this.scale = scale;
        this.zIndex = zIndex;
    }

    public LevelObjectType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Vector2d getScale() {
        return scale;
    }

    public int getZIndex() {
        return zIndex;
    }
}
