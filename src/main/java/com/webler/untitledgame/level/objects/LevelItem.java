package com.webler.untitledgame.level.objects;

import com.webler.goliath.graphics.Sprite;
import com.webler.untitledgame.level.enums.LevelObjectType;
import lombok.Getter;
import org.joml.Vector2d;

@Getter
public class LevelItem extends LevelObject {
    private final String description;
    private final int price;

    public LevelItem(String identifier, String name, Sprite sprite, Vector2d scale, int zIndex, String description, int price) {
        super(LevelObjectType.ITEM, identifier, name, sprite, scale, zIndex);
        this.description = description;
        this.price = price;
    }
}
