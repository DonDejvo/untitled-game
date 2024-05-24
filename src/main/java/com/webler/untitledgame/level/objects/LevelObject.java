package com.webler.untitledgame.level.objects;

import com.webler.goliath.graphics.Sprite;
import com.webler.untitledgame.level.enums.LevelObjectType;
import lombok.Getter;
import org.joml.Vector2d;

@Getter
public abstract class LevelObject {
    private final LevelObjectType type;
    private final String identifier;
    private final String name;
    private final Sprite sprite;
    private final Vector2d scale;
    private final int zIndex;

    public LevelObject(LevelObjectType type, String identifier, String name, Sprite sprite, Vector2d scale, int zIndex) {
        this.type = type;
        this.identifier = identifier;
        this.name = name;
        this.sprite = sprite;
        this.scale = scale;
        this.zIndex = zIndex;
    }
}
