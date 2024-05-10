package com.webler.untitledgame.components;

import com.webler.goliath.graphics.Sprite;
import org.joml.Vector2d;

public class LevelItem extends LevelObject {
    private String description;
    private int price;

    public LevelItem(String identifier, String name, Sprite sprite, Vector2d scale, int zIndex, String description, int price) {
        super(LevelObjectType.ITEM, identifier, name, sprite, scale, zIndex);
        this.description = description;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }
}