package com.webler.goliath.eventsystem.events;

import com.webler.goliath.core.GameObject;
import lombok.Getter;

@Getter
public abstract class Event {
    private final GameObject gameObject;

    public Event(GameObject gameObject) {
        this.gameObject = gameObject;
    }

}