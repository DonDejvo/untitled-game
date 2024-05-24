package com.webler.untitledgame.level.objects;

import com.webler.goliath.graphics.Sprite;
import com.webler.untitledgame.level.enums.LevelObjectType;
import org.joml.Vector2d;

public class LevelEntity extends LevelObject {

    public LevelEntity(String identifier, String name, Sprite sprite, Vector2d scale, int zIndex) {
        super(LevelObjectType.ENTITY, identifier, name, sprite, scale, zIndex);
    }
}
