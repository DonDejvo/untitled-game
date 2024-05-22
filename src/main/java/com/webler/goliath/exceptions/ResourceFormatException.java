package com.webler.goliath.exceptions;

public class ResourceFormatException extends IllegalStateException {
    public ResourceFormatException(String resourceName, String message) {
        super("Resource " + resourceName + " format error: " + message);
    }
}
