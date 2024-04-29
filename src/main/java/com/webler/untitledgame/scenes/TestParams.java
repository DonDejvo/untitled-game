package com.webler.untitledgame.scenes;

import com.webler.goliath.core.SceneParams;

public class TestParams extends SceneParams {
    private String levelPath;

    public TestParams(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getLevelPath() {
        return levelPath;
    }
}
