package com.webler.untitledgame.level.controllers;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.time.TimeoutEvent;
import com.webler.goliath.time.Timer;

public class ExplosionLightController extends Component {
    private Timer timer;
    private SpotLight light;

    public ExplosionLightController(Timer timer, SpotLight light) {
        this.timer = timer;
        this.light = light;
    }

    @Override
    public void start() {
        timer.startTimer(0.125);
    }

    @Override
    public void update(double dt) {
        light.setRadiusMin(1 * timer.getRemainingTime() / timer.getWaitTime());
        light.setRadiusMax(2 * timer.getRemainingTime() / timer.getWaitTime());
    }

    @Override
    public void destroy() {

    }

    @EventHandler
    public void onTimeout(TimeoutEvent event) {
        if(event.getGameObject() == gameObject) {
            gameObject.remove();
        }
    }
}
