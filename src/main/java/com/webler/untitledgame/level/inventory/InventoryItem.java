package com.webler.untitledgame.level.inventory;

import com.webler.untitledgame.components.LevelItem;

public class InventoryItem {
    private LevelItem levelItem;

    public InventoryItem(LevelItem levelItem) {
        this.levelItem = levelItem;
    }

    public LevelItem getLevelItem() {
        return levelItem;
    }
}
