package com.webler.goliath.time;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.eventsystem.events.Event;

public class TimeoutEvent extends Event {
    public TimeoutEvent(GameObject gameObject) {
        super(gameObject);
    }
}
