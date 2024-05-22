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

    /**
    * Returns the position of this vector. This is equivalent to x y in Java2D ( 0 0 ).
    * 
    * 
    * @return the position of this vector as a Vector2d object with the same x and y values as this
    */
    public Vector2d getPosition() {
        return new Vector2d(x, y);
    }

    /**
    * Returns the size of this rectangular area. The returned vector is backed by this rectangular area and will have the same width and height as this rectangular area.
    * 
    * 
    * @return the size of this rectangular area as a Vector2d object ( never null ). Changes to this object's data will not be reflected in the returned vector
    */
    public Vector2d getSize() {
        return new Vector2d(width, height);
    }

    /**
    * Returns true if this rectangle intersects the specified rectangle. This method is equivalent to intersect ( rect ) in Java 8 but faster
    * 
    * @param rect - the rectangle to test for intersection
    * 
    * @return true if and only if this rectangle intersects the specified rectangle ; false otherwise or if the rectangles do not
    */
    public boolean intersects(Rect rect) {
        return (x + width - rect.x) * (x - (rect.x + rect.width)) < 0 &&
                (y + height - rect.y) * (y - (rect.y + rect.height)) < 0;
    }

    /**
    * Returns true if the point is contained within this rectangle. This is equivalent to checking if the point lies within the rectangle's bounding box.
    * 
    * @param point - the point to check. It must be non - null and in the range [ 0 0 ].
    * 
    * @return true if the point is contained within this rectangle false otherwise ( not necessarily true for all points of the rectangle
    */
    public boolean contains(Vector2d point) {
        return point.x > x && point.x < x + width &&
                point.y > y && point.y < y + height;
    }
}
