package com.webler.goliath.dialogs.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class DialogNextEvent extends Event {
    private final String name;

    public DialogNextEvent(GameObject gameObject, String name) {
        super(gameObject);
        this.name = name;
    }

    public String getDialogName() {
        return name;
    }
}
