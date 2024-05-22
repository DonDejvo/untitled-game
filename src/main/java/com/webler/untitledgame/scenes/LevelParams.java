package com.webler.untitledgame.scenes;

import com.webler.goliath.core.SceneParams;
import lombok.Getter;

@Getter
public class LevelParams extends SceneParams {
    private final String levelPath;

    public LevelParams(String levelPath) {
        this.levelPath = levelPath;
    }

}
