package com.webler.goliath.graphics.geometry;

import com.webler.goliath.graphics.DrawCall;
import com.webler.goliath.graphics.Geometry;

public class Cube extends Geometry {
    private static final float[] vertices = new float[] {
            // front
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            // back
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f,
            // left
            -0.5f, 0.5f, -0.5f, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f,
            // right
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,
            // top
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            // bottom
            -0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f,
    };
    private static final int[] indices = new int[] {
            // front
            0, 1, 2,
            0, 2, 3,
            // back
            4, 5, 6,
            4, 6, 7,
            // left
            8, 9, 10,
            8, 10, 11,
            // right
            12, 13, 14,
            12, 14, 15,
            // top
            16, 17, 18,
            16, 18, 19,
            // bottom
            20, 21, 22,
            20, 22, 23,
    };
    private final DrawCall[] drawCalls;

    public Cube(int texId) {
        drawCalls = new DrawCall[] {
                new DrawCall(0, 36, texId)
        };
    }

    public Cube(int[] texIds) {
        drawCalls = new DrawCall[6];
        for (int i = 0; i < 6; ++i) {
            drawCalls[i] = new DrawCall(i * 6, 6, texIds[i]);
        }
    }

    @Override
    public float[] getVertices() {
        return vertices;
    }

    @Override
    public int[] getIndices() {
        return indices;
    }

    @Override
    public DrawCall[] getDrawCalls() {
        return drawCalls;
    }
}
