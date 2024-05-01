package com.webler.untitledgame.scenes;

import com.webler.goliath.core.SceneParams;

public class LevelParams extends SceneParams {
    private String levelPath;

    public LevelParams(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getLevelPath() {
        return levelPath;
    }
}
