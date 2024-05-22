package com.webler.untitledgame.level.controllers;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.time.TimeoutEvent;
import com.webler.goliath.time.Timer;

public class ExplosionLightController extends Component {
    private final Timer timer;
    private final SpotLight light;

    public ExplosionLightController(Timer timer, SpotLight light) {
        this.timer = timer;
        this.light = light;
    }

    /**
    * Starts the timer. This is called by the start method of the PlaybackService and should not be called by user
    */
    @Override
    public void start() {
        timer.startTimer(0.125);
    }

    /**
    * Updates the radius of the light. This is called every frame to update the properties of the light.
    * 
    * @param dt - time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        light.setRadiusMin(1 * timer.getRemainingTime() / timer.getWaitTime());
        light.setRadiusMax(8 * timer.getRemainingTime() / timer.getWaitTime());
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Called when the timeout occurs. This is a no - op to prevent an attacker from trying to play the game in the middle of a timeout.
    * 
    * @param event - The event that triggered the timeout. Must not be null
    */
    @EventHandler
    @SuppressWarnings("unused")
    public void onTimeout(TimeoutEvent event) {
        // Remove the game object from the game object.
        if(event.getGameObject() == gameObject) {
            gameObject.remove();
        }
    }
}
