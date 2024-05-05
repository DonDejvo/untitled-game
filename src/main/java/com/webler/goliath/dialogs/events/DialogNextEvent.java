package com.webler.goliath.dialogs.events;

import com.webler.goliath.eventsystem.events.Event;

public class DialogNextEvent extends Event {
    private final String name;

    public DialogNextEvent(String name) {
        this.name = name;
    }

    public String getDialogName() {
        return name;
    }
}
