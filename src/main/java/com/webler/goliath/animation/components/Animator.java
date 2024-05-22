package com.webler.goliath.animation.components;

import com.webler.goliath.animation.Animable;
import com.webler.goliath.animation.Animation;
import com.webler.goliath.animation.Frame;
import com.webler.goliath.core.Component;

public class Animator extends Component {
    private final Animable animable;
    private boolean isPlaying;
    private Animation currentAnimation;
    private int currentFrameIdx;
    private double counter;
    private boolean isLoop;

    public Animator(Animable animable) {
        this.animable = animable;
        isPlaying = false;
        currentFrameIdx = 0;
        counter = 0;
        isLoop = false;
    }

    @Override
    public void start() {

    }

    /**
    * Updates the animation. This is called every frame by the animator. If the animation is playing it will start a new animation from the current frame
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        // This method is called by the animation.
        if (isPlaying) {
            Frame currentFrame = currentAnimation.frames()[currentFrameIdx];
            animable.setFrame(currentFrame.x(), currentFrame.y(), currentAnimation.frameWidth(), currentAnimation.frameHight());
            // This method is called by the animation.
            if(counter >= currentFrame.durationInMillis()) {
                counter = 0;
                ++currentFrameIdx;
                // If the animation is currently playing.
                if(currentFrameIdx > currentAnimation.frames().length - 1) {
                    // If the loop is true the current frame is reset to 0.
                    if(isLoop) {
                        currentFrameIdx = 0;
                    } else {
                        isPlaying = false;
                    }
                }
            }
            counter += dt;
        }
    }

    @Override
    public void destroy() {

    }

    /**
    * Plays the given animation. This is equivalent to starting a new animation and setting the current frame to 0
    * 
    * @param animation - The animation to play.
    * @param loop - Whether or not to loop the animation ( true ) or play it ( false
    */
    public void playAnim(Animation animation, boolean loop) {
        currentAnimation = animation;
        isLoop = loop;
        isPlaying = true;
        counter = 0;
        currentFrameIdx = 0;
    }

    /**
    * Plays an animation if it is not playing. This is a convenience method that calls #playAnim ( Animation boolean ).
    * 
    * @param animation - The animation to play. Must not be null.
    * @param loop - Whether or not to loop the animation after it is played
    */
    public void playAnimIfNotPlaying(Animation animation, boolean loop) {
        // Play the animation if it is playing.
        if(!isPlaying(animation.name())) {
            playAnim(animation, loop);
        }
    }

    /**
    * Checks if the animation with the given name is playing. This is useful for debugging and to avoid accidentally getting a reference to the animation that is being played.
    * 
    * @param animationName - The name of the animation to check.
    * 
    * @return True if the animation with the given name is playing false otherwise. Note that this does not return true if the animation is playing
    */
    public boolean isPlaying(String animationName) {
        return isPlaying && animationName.equals(currentAnimation.name());
    }
}
