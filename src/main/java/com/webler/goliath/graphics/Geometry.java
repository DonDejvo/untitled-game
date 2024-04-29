package com.webler.goliath.graphics;

public abstract class Geometry {

    public abstract float[] getVertices();

    public abstract int[] getIndices();

    public abstract DrawCall[] getDrawCalls();
}
