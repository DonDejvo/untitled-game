package com.webler.untitledgame.level.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;
import lombok.Getter;

@Getter
public class ItemUnselectedEvent extends Event {
    private final String itemName;

    public ItemUnselectedEvent(GameObject gameObject, String itemName) {
        super(gameObject);
        this.itemName = itemName;
    }
}
