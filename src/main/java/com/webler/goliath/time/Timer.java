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

    /**
    * Updates the timer. This is called every frame to update the timer. If the timer is over the waitTime it will fire a TimeoutEvent
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        // This method is called by the game object when the game is paused.
        if(!paused) {
            counter += dt;
            // This method is called when the game object is in a timeout.
            if(counter >= waitTime) {
                EventManager.dispatchEvent(new TimeoutEvent(gameObject));
            }
        }
    }

    @Override
    public void destroy() {

    }

    /**
    * Starts the timer. This is a no - op if the timer is already running. The waitTime should be in seconds
    * 
    * @param waitTime - the time to wait between
    */
    public void startTimer(double waitTime) {
        paused = false;
        this.waitTime = waitTime;
    }

    /**
    * Pause the timer. This is called when the timer is no longer needed to run a test or when an error occurs
    */
    public void pauseTimer() {
        paused = true;
    }

    /**
    * Returns the amount of time remaining to be spent in this event. This can be used to determine how long the event has been processed before it is considered to be finished.
    * 
    * 
    * @return the amount of time remaining to be spent in this event before it is considered to be finished ( in seconds
    */
    public double getRemainingTime() {
        return waitTime - counter;
    }

}
