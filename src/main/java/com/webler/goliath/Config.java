package com.webler.goliath;

import com.webler.goliath.core.SceneParams;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config {
    private String title;
    private int windowWidth;
    private int windowHeight;
    private String startScene;
    private SceneParams startSceneParams;
    private boolean loggerEnabled;

    public Config(String title, int windowWidth, int windowHeight, String startScene, SceneParams startSceneParams, boolean loggerEnabled) {
        this.title = title;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.startScene = startScene;
        this.startSceneParams = startSceneParams;
        this.loggerEnabled = loggerEnabled;
    }

    public void preload() {}
}
