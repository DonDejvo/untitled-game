package com.webler.untitledgame.level.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class DoorOpened extends Event {

    public DoorOpened(GameObject door) {
        super(door);
    }

    public GameObject getDoor() {
        return getGameObject();
    }
}
