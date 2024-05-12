package com.webler.untitledgame.level.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class DoorOpenedEvent extends Event {

    public DoorOpenedEvent(GameObject door) {
        super(door);
    }

    public GameObject getDoor() {
        return getGameObject();
    }
}
