package com.webler.untitledgame.components;

import com.webler.goliath.graphics.Sprite;
import lombok.Getter;
import org.joml.Vector2d;

public abstract class LevelObject {
    @Getter
    private LevelObjectType type;
    @Getter
    private String identifier;
    @Getter
    private String name;
    @Getter
    private Sprite sprite;
    @Getter
    private Vector2d scale;
    @Getter
    private int zIndex;

    public LevelObject(LevelObjectType type, String identifier, String name, Sprite sprite, Vector2d scale, int zIndex) {
        this.type = type;
        this.identifier = identifier;
        this.name = name;
        this.sprite = sprite;
        this.scale = scale;
        this.zIndex = zIndex;
    }
}
