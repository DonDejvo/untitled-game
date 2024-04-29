package com.webler.goliath.math;

import org.joml.Vector2d;

public class Rect {
    public double x;
    public double y;
    public double width;
    public double height;

    public Rect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Vector2d getPosition() {
        return new Vector2d(x, y);
    }

    public Vector2d getSize() {
        return new Vector2d(width, height);
    }

    public boolean intersects(Rect rect) {
        return (x + width - rect.x) * (x - (rect.x + rect.width)) < 0 &&
                (y + height - rect.y) * (y - (rect.y + rect.height)) < 0;
    }

    public boolean contains(Vector2d point) {
        return point.x > x && point.x < x + width &&
                point.y > y && point.y < y + height;
    }
}
