package com.webler.goliath.core.exceptions;

public class ComponentTypeException extends IllegalStateException {
    public ComponentTypeException(String componentName, String componentType) {
        super("Component " + componentName + " of type " + componentType + " is not supported");
    }
}
