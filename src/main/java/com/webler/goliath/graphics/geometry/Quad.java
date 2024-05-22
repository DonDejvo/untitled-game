package com.webler.goliath.graphics.geometry;

import com.webler.goliath.graphics.DrawCall;
import com.webler.goliath.graphics.Geometry;

public class Quad extends Geometry {
    private static final float[] vertices = new float[] {
            -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };
    private static final int[] indices = new int[] {
            0, 1, 2,
            0, 2, 3
    };
    private final DrawCall[] drawCalls;

    public Quad(int texId) {
         drawCalls = new DrawCall[] {
                new DrawCall(0, 6, texId)
        };
    }

    /**
    * Returns the vertices of the polygon. This is a copy of the array returned by #getVertices ()
    */
    @Override
    public float[] getVertices() {
        return vertices;
    }

    /**
    * Returns the indices of the elements. This is a copy of the indices array that can be manipulated
    */
    @Override
    public int[] getIndices() {
        return indices;
    }

    /**
    * Returns an array of DrawCalls that this Drawable can draw. The order of the array is undefined and may change
    */
    @Override
    public DrawCall[] getDrawCalls() {
        return drawCalls;
    }
}
