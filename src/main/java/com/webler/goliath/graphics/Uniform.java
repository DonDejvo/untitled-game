package com.webler.goliath.graphics;

public record Uniform(String name, int location, String type) {

    public boolean equals(Uniform uniform) {
        return location == uniform.location;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return location;
    }
}

