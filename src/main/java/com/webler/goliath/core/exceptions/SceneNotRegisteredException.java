package com.webler.goliath.core.exceptions;

public class SceneNotRegisteredException extends IllegalStateException {
    public SceneNotRegisteredException(String sceneName) {
        super("Scene " + sceneName + " not registered.");
    }
}
