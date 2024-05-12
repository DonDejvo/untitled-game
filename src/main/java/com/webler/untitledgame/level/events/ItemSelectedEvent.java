package com.webler.untitledgame.level.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class ItemSelectedEvent extends Event {
    private String itemName;

    public ItemSelectedEvent(GameObject gameObject, String itemName) {
        super(gameObject);
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }
}
