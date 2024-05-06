package com.webler.goliath.dialogs.events;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class DialogEndedEvent extends Event {

    public DialogEndedEvent(GameObject gameObject) {
        super(gameObject);
    }
}
