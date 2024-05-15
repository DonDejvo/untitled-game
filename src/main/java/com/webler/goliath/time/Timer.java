package com.webler.goliath.time;

import com.webler.goliath.core.Component;
import com.webler.goliath.eventsystem.EventManager;
import lombok.Getter;

public class Timer extends Component {
    private double counter;
    @Getter
    private double waitTime;
    @Getter
    private boolean paused;

    public Timer() {
        counter = 0;
        paused = true;
        waitTime = 1;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        if(!paused) {
            counter += dt;
            if(counter >= waitTime) {
                EventManager.dispatchEvent(new TimeoutEvent(gameObject));
            }
        }
    }

    @Override
    public void destroy() {

    }

    public void startTimer(double waitTime) {
        paused = false;
        this.waitTime = waitTime;
    }

    public void pauseTimer() {
        paused = true;
    }

    public double getRemainingTime() {
        return waitTime - counter;
    }

}
