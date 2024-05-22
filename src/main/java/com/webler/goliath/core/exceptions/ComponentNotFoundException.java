package com.webler.goliath.core.exceptions;

public class ComponentNotFoundException extends IllegalStateException {
    public ComponentNotFoundException(String gameObjectName, String componentName) {
        super("Component " + componentName + " not found in GameObject " + gameObjectName);
    }
}
