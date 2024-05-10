package com.webler.goliath.exceptions;

public class ResourceNotFoundException extends IllegalStateException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
