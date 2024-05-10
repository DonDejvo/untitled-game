package com.webler.goliath.animation.components;

import com.webler.goliath.animation.Animable;
import com.webler.goliath.animation.Animation;
import com.webler.goliath.animation.Frame;
import com.webler.goliath.core.Component;

public class Animator extends Component {
    private Animable animable;
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

    @Override
    public void update(double dt) {
        if (isPlaying) {
            Frame currentFrame = currentAnimation.getFrames()[currentFrameIdx];
            animable.setFrame(currentFrame.x(), currentFrame.y(), currentAnimation.getFrameWidth(), currentAnimation.getFrameHight());
            if(counter >= currentFrame.durationInMillis()) {
                counter = 0;
                ++currentFrameIdx;
                if(currentFrameIdx > currentAnimation.getFrames().length - 1) {
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

    public void playAnim(Animation animation, boolean loop) {
        currentAnimation = animation;
        isLoop = loop;
        isPlaying = true;
        counter = 0;
        currentFrameIdx = 0;
    }
}
