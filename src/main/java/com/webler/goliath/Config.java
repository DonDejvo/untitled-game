package com.webler.goliath;

import com.webler.goliath.core.SceneParams;

public record Config(String title, int windowWidth, int windowHeight, String startScene, SceneParams startSceneParams) { }
