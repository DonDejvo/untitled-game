package com.webler.goliath.eventsystem.events;

import com.webler.goliath.core.GameObject;

public abstract class Event {
    private GameObject gameObject;

    public Event(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return gameObject;
    }
}