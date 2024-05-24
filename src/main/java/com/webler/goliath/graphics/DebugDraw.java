package com.webler.goliath.graphics;

import com.webler.goliath.utils.AssetPool;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL30.*;

public class DebugDraw {
    private static DebugDraw instance = null;
    private static final int MAX_LINES = 10000;
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

    /**
    * Returns the singleton instance of DebugDraw. This is useful for debugging purposes. If you want to run this on a test environment you should use #get () instead.
    * 
    * 
    * @return the singleton instance of DebugDraw or null if none exists in the current JVM ( which can be the case in tests
    */
    public static DebugDraw get() {
        // Create a new DebugDraw instance.
        if(instance == null) {
            instance = new DebugDraw();
        }
        return instance;
    }

    public DebugDraw() {
        vertices = new float[MAX_LINES * 2 * VERT_SIZE];
    }

    /**
    * Starts the rendering. This is called by OpenGL at start and should not be called by user code
    */
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

    /**
    * Adds a line to the graph. This is useful for drawing lines that don't fit in the graph's view.
    * 
    * @param from - The position of the line from the origin.
    * @param to - The position of the line to the destination.
    * @param color - The color of the line in the form RGB
    */
    public void addLine(Vector3d from, Vector3d to, Color color) {
        // Returns the number of lines to be processed.
        if(linesCount + 1 == MAX_LINES) {
            return;
        }
        float[] positions = new float[] {
                (float) from.x, (float) from.y, (float) from.z,
                (float) to.x, (float) to.y, (float) to.z
        };
        float[] colorArray = color.toArray();
        // Copy all the vertices and color values from the vertices and color array.
        for (int i = 0; i < 2; i++) {
            // Set the position of the vertices.
            for (int j = 0; j < POS_SIZE; ++j) {
                vertices[(linesCount * 2 + i) * VERT_SIZE + POS_OFFSET + j] = positions[i * POS_SIZE + j];
            }
            System.arraycopy(colorArray, 0, vertices, (linesCount * 2 + i) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
        }
        ++linesCount;
    }

    /**
    * Adds a rectangle to the path. The rectangle is drawn from the center of the path and has the specified size and color
    * 
    * @param position - The x and y coordinates of the rectangle's center
    * @param size - The width and height of the rectangle's center
    * @param color - The color of the rectangle ( null for none
    */
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

    public void addBox(Vector3d position, Vector3d size, Color color) {
        double left = position.x - size.x * 0.5;
        double top = position.y - size.y * 0.5;
        double right = position.x + size.x * 0.5;
        double bottom = position.y + size.y * 0.5;
        double near = position.z + size.z * 0.5;
        double far = position.z - size.z * 0.5;

        addLine(new Vector3d(left, top, near), new Vector3d(right, top, near), color);
        addLine(new Vector3d(left, bottom, near), new Vector3d(right, bottom, near), color);
        addLine(new Vector3d(left, top, near), new Vector3d(left, bottom, near), color);
        addLine(new Vector3d(right, bottom, near), new Vector3d(right, top, near), color);

        addLine(new Vector3d(left, top, far), new Vector3d(right, top, far), color);
        addLine(new Vector3d(left, bottom, far), new Vector3d(right, bottom, far), color);
        addLine(new Vector3d(left, top, far), new Vector3d(left, bottom, far), color);
        addLine(new Vector3d(right, bottom, far), new Vector3d(right, top, far), color);

        addLine(new Vector3d(left, top, near), new Vector3d(left, top, far), color);
        addLine(new Vector3d(right, top, near), new Vector3d(right, top, far), color);
        addLine(new Vector3d(right, bottom, near), new Vector3d(right, bottom, far), color);
        addLine(new Vector3d(left, bottom, near), new Vector3d(left, bottom, far), color);
    }

    /**
    * Adds a cross to the shape. The cross is defined by a radius and a color. This can be used to draw a circle at a specific position.
    * 
    * @param position - The center of the cross. It should be normalized to match the shape's origin.
    * @param radius - The radius of the cross. This is in the range [ 0 1 ].
    * @param color - The color of the cross. This color should be a member of the Color class
    */
    public void addCross(Vector3d position, double radius, Color color) {
        addLine(new Vector3d(position.x - radius, position.y, position.z), new Vector3d(position.x + radius, position.y, position.z), color);
        addLine(new Vector3d(position.x, position.y - radius, position.z), new Vector3d(position.x, position.y + radius, position.z), color);
        addLine(new Vector3d(position.x, position.y, position.z - radius), new Vector3d(position.x, position.y, position.z + radius), color);
    }

    /**
    * Begin a new frame. This is called at the beginning of each frame to reset the line count to
    */
    public void beginFrame() {
        linesCount = 0;
    }

    /**
    * Destroys OpenGL resources. This is called by #destroy ( GLContext ) when the context is no longer needed
    */
    public void destroy() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

    /**
    * Draws the lines using the given projection matrix. This is useful for debugging and to visualize the lines as they are drawn.
    * 
    * @param PV - The projection matrix to use for drawing the lines
    */
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
