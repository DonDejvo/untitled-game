package com.webler.untitledgame.level.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class DoorOpened extends Event {
    private final GameObject door;

    public DoorOpened(GameObject door) {
        this.door = door;
    }

    public GameObject getDoor() {
        return door;
    }
}
