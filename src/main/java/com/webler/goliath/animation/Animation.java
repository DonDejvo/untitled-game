package com.webler.goliath.animation;

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

    public String getName() {
        return name;
    }

    public Frame[] getFrames() {
        return frames;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHight() {
        return frameHight;
    }
}
