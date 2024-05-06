package com.webler.untitledgame.components;

import com.webler.goliath.graphics.Sprite;
import org.joml.Vector2d;

public class LevelEntity extends LevelObject {

    public LevelEntity(String identifier, String name, Sprite sprite, Vector2d scale, int zIndex) {
        super(LevelObjectType.ENTITY, identifier, name, sprite, scale, zIndex);
    }
}
