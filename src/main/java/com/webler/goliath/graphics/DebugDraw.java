package com.webler.goliath.graphics;

import com.webler.goliath.utils.AssetPool;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL30.*;

public class DebugDraw {
    private static DebugDraw instance = null;
    private static final int MAX_LINES = 1000;
    private static final int POS_INDEX = 0;
    private static final int POS_SIZE = 3;
    private static final int POS_OFFSET = 0;
    private static final int COLOR_INDEX = 1;
    private static final int COLOR_SIZE = 4;
    private static final int COLOR_OFFSET = 3;
    private static final int VERT_SIZE = POS_SIZE + COLOR_SIZE;
    private int vao;
    private int vbo;
    private Shader shader;
    private int linesCount;
    private final float[] vertices;

    public static DebugDraw get() {
        if(instance == null) {
            instance = new DebugDraw();
        }
        return instance;
    }

    public DebugDraw() {
        vertices = new float[MAX_LINES * 2 * VERT_SIZE];
    }

    public void start() {
        shader = AssetPool.getShader("goliath/shaders/lines.glsl");
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, MAX_LINES * 2 * VERT_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(
                POS_INDEX,
                POS_SIZE,
                GL_FLOAT,
                false,
                VERT_SIZE * Float.BYTES,
                POS_OFFSET * Float.BYTES);
        glVertexAttribPointer(
                COLOR_INDEX,
                COLOR_SIZE,
                GL_FLOAT,
                false,
                VERT_SIZE * Float.BYTES,
                COLOR_OFFSET * Float.BYTES);

        glBindVertexArray(0);
    }

    public void addLine(Vector3d from, Vector3d to, Color color) {
        if(linesCount + 1 == MAX_LINES) {
            return;
        }
        float[] positions = new float[] {
                (float) from.x, (float) from.y, (float) from.z,
                (float) to.x, (float) to.y, (float) to.z
        };
        float[] colorArray = color.toArray();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < POS_SIZE; ++j) {
                vertices[(linesCount * 2 + i) * VERT_SIZE + POS_OFFSET + j] = positions[i * POS_SIZE + j];
            }
            System.arraycopy(colorArray, 0, vertices, (linesCount * 2 + i) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
        }
        ++linesCount;
    }

    public void addRect(Vector2d position, Vector2d size, Color color) {
        double left = position.x - size.x * 0.5;
        double top = position.y - size.y * 0.5;
        double right = position.x + size.x * 0.5;
        double bottom = position.y + size.y * 0.5;
        addLine(new Vector3d(left, top, 0), new Vector3d(right, top, 0), color);
        addLine(new Vector3d(left, bottom, 0), new Vector3d(right, bottom, 0), color);
        addLine(new Vector3d(left, top, 0), new Vector3d(left, bottom, 0), color);
        addLine(new Vector3d(right, bottom, 0), new Vector3d(right, top, 0), color);
    }

    public void beginFrame() {
        linesCount = 0;
    }

    public void destroy() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

    public void draw(Matrix4d PV) {
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glUseProgram(shader.getProgram());

        shader.supplyUniform("u_PV", PV);

        glBindVertexArray(vao);

        glEnableVertexAttribArray(POS_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);

        glDrawArrays(GL_LINES, 0, linesCount * 2);

        glDisableVertexAttribArray(POS_INDEX);
        glDisableVertexAttribArray(COLOR_INDEX);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
