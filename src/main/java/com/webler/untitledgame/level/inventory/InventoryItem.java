package com.webler.untitledgame.level.inventory;

import com.webler.goliath.graphics.Sprite;

public class InventoryItem {
    private String title;
    private Sprite sprite;
    private String description;

    public InventoryItem(String name, Sprite sprite, String description) {
        this.title = name;
        this.sprite = sprite;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public String getDescription() {
        return description;
    }
}
