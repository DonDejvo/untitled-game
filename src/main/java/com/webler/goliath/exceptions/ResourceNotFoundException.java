package com.webler.goliath.exceptions;

public class ResourceNotFoundException extends IllegalStateException {
    public ResourceNotFoundException(String resourceName) {
        super("Resource not found: " + resourceName);
    }
}
