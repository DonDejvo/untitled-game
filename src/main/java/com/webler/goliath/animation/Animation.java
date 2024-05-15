package com.webler.goliath.animation;

import lombok.Getter;

@Getter
public class Animation {
    private String name;
    private int frameWidth, frameHight;
    private Frame[] frames;

    public Animation(String name, int frameWidth, int frameHight, Frame[] frames) {
        this.name = name;
        this.frameWidth = frameWidth;
        this.frameHight = frameHight;
        this.frames = frames;
    }

}
