package com.webler.goliath.dialogs.events;

import com.webler.goliath.eventsystem.events.Event;

public class DialogNext extends Event {
    private final String name;

    public DialogNext(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
