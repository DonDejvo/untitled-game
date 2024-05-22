package com.webler.goliath.graphics;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.Rect;
import lombok.Getter;
import org.joml.Matrix4d;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class SpriteBatch {
    private static final int MAX_SPRITES = 1000;
    private static final int POS_INDEX = 0;
    private static final int POS_SIZE = 3;
    private static final int POS_OFFSET = 0;
    private static final int UV_INDEX = 1;
    private static final int UV_SIZE = 2;
    private static final int UV_OFFSET = 3;
    private static final int COLOR_INDEX = 2;
    private static final int COLOR_SIZE = 4;
    private static final int COLOR_OFFSET = 5;
    private static final int VERT_SIZE = POS_SIZE + UV_SIZE + COLOR_SIZE;
    private int vao;
    private int vbo;
    private int ebo;
    private final List<SpriteRenderer> spriteRenderers;
    private final List<DrawCall> drawCalls;
    @Getter
    private final int zIndex;

    public SpriteBatch(int zIndex) {
        this.zIndex = zIndex;
        spriteRenderers = new ArrayList<>();
        drawCalls = new ArrayList<>();
    }

    /**
    * Adds a SpriteRenderer to the list of SpriteRenderers. This is useful for adding custom SpriteRenderer instances that are specific to the game.
    * 
    * @param spriteRenderer - The SpriteRenderer to add to the
    */
    public void add(SpriteRenderer spriteRenderer) {
        spriteRenderers.add(spriteRenderer);
    }

    /**
    * Removes a SpriteRenderer from the list. This will return true if the SpriteRenderer was removed false otherwise.
    * 
    * @param spriteRenderer - The SpriteRenderer to remove.
    * 
    * @return True if the SpriteRenderer was removed false otherwise ( since there is no way to determine if the SpriteRenderer was in the list
    */
    public boolean remove(SpriteRenderer spriteRenderer) {
        return spriteRenderers.remove(spriteRenderer);
    }

    /**
    * Initializes the OpenGL state. This is called by the start method of the SpriteRenderer. You can call it any time you want to start drawing
    */
    public void start() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, MAX_SPRITES * 4 * VERT_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(POS_INDEX,
                POS_SIZE,
                GL_FLOAT,
                false,
                VERT_SIZE * Float.BYTES,
                POS_OFFSET * Float.BYTES);
        glVertexAttribPointer(UV_INDEX,
                UV_SIZE,
                GL_FLOAT,
                false,
                VERT_SIZE * Float.BYTES,
                UV_OFFSET * Float.BYTES);
        glVertexAttribPointer(COLOR_INDEX,
                COLOR_SIZE,
                GL_FLOAT,
                false,
                VERT_SIZE * Float.BYTES,
                COLOR_OFFSET * Float.BYTES);

        int[] indexCache = new int[] {
                0, 1, 2,
                0, 2, 3
        };
        int[] indices = new int[MAX_SPRITES * 6];
        // This method is used to store the indices of the index cache.
        for (int i = 0; i < MAX_SPRITES; ++i) {
            // Set the indices of the indices in the cache.
            for(int j = 0; j < indexCache.length; ++j) {
                indices[i * 6 + j] = indexCache[j] + i * 4;
            }
        }

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    // TODO: Do only if needed
    /**
    * Initializes the buffers. This is called by Scene#begin ( Graphics ) and Scene#end () to ensure that buffers are initialized
    */
    public void initBuffers() {

        drawCalls.clear();

        // Returns true if there are no sprite renderers.
        if(spriteRenderers.isEmpty()) return;

        ArrayList<SpriteRenderer> visibleRenderers = getVisibleRenderers();

        float[] vertices = new float[visibleRenderers.size() * 4 * VERT_SIZE];

        // Draw all the visible renderers.
        for(int i = 0; i < visibleRenderers.size(); ++i) {
            SpriteRenderer spriteRenderer = visibleRenderers.get(i);
            Sprite sprite = spriteRenderer.getSprite();

            float[] positions = getPositions(spriteRenderer);
            float[] uvs = sprite.getTexCoords();
            float[] color = spriteRenderer.getColor().toArray();
            // Copy all the vertices and uvs from the vertices and color.
            for(int j = 0; j < 4; ++j) {
                // Set the position of the vertices in the graph.
                for(int k = 0; k < POS_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + POS_OFFSET + k] = positions[j * POS_SIZE + k];
                }
                // Set the vertices of the vertex.
                for(int k = 0; k < UV_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + UV_OFFSET + k] = uvs[j * UV_SIZE + k];
                }
                System.arraycopy(color, 0, vertices, (i * 4 + j) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
            }

            // Add a DrawCall to the drawCalls list.
            if(i == visibleRenderers.size() - 1 || !sprite.getTexture().equals(visibleRenderers.get(i + 1).getSprite().getTexture())) {
                // Add a DrawCall to the drawCalls list.
                if(drawCalls.isEmpty()) {
                    drawCalls.add(new DrawCall(0, (i + 1) * 6, sprite.getTexture().getTexId()));
                } else {
                    DrawCall prevDrawCall = drawCalls.get(drawCalls.size() - 1);
                    int offset = prevDrawCall.offset() + prevDrawCall.count();
                    int count = (i + 1) * 6 - offset;
                    drawCalls.add(new DrawCall(offset, count, sprite.getTexture().getTexId()));
                }
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    /**
    * Renders the VAO. This is called by OpenGL every frame to render the vertex data to the screen
    */
    public void render() {
        initBuffers();

        glBindVertexArray(vao);

        glEnableVertexAttribArray(POS_INDEX);
        glEnableVertexAttribArray(UV_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);

        for (DrawCall drawCall : drawCalls) {
            glBindTexture(GL_TEXTURE_2D, drawCall.texId());
            glDrawElements(GL_TRIANGLES, drawCall.count(), GL_UNSIGNED_INT, (long) drawCall.offset() * Integer.BYTES);
        }

        glDisableVertexAttribArray(POS_INDEX);
        glDisableVertexAttribArray(UV_INDEX);
        glDisableVertexAttribArray(COLOR_INDEX);

        glBindVertexArray(0);
    }

    /**
    * Destroys OpenGL resources. This is called by #destroy ( GLContext ) when the context is no longer needed
    */
    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    /**
    * Returns true if the queue is full. This is used to determine if we should try to render a new tile or not.
    * 
    * 
    * @return whether or not the queue is full or not ( true or false ). Note that false is returned if there are too many sprites
    */
    public boolean isFull() {
        return spriteRenderers.size() == MAX_SPRITES;
    }

    /**
    * Returns a list of SpriteRenderers that are visible to the camera. The list is sorted by Z - order and the Sprite's texture ID is used as the key to the array list.
    * 
    * 
    * @return An ArrayList of SpriteRenderer that are visible to the camera sorted by Z - order and the Sprite's texture ID
    */
    private ArrayList<SpriteRenderer> getVisibleRenderers() {
        Camera camera = spriteRenderers.get(0).getGameObject().getScene().getCamera();

        ArrayList<SpriteRenderer> visibleRenderers = new ArrayList<>(spriteRenderers);

        // Returns a list of visible renderers.
        if(zIndex == -1) {
            visibleRenderers.sort((a, b) -> {
                double distToCam1 = a.getOffsetPosition().distance(camera.getGameObject().transform.position);
                double distToCam2 = b.getOffsetPosition().distance(camera.getGameObject().transform.position);
                return Double.compare(distToCam2, distToCam1);
            });
        } else {
            Rect cameraBoundingRect = camera.getViewport();
            visibleRenderers = visibleRenderers.stream()
                    .filter(a -> {
                        Rect boundingRect = a.getBoundingRect();
                        return boundingRect.intersects(cameraBoundingRect);
                    })
                    .sorted(Comparator.comparingInt(a -> a.getSprite().getTexture().getTexId()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return visibleRenderers;
    }

    /**
    * Returns the positions of the sprites. This is used to calculate the world coordinates of the sprites.
    * 
    * @param spriteRenderer - The SpriteRenderer to calculate the positions
    */
    private float[] getPositions(SpriteRenderer spriteRenderer) {
        Transform transform = spriteRenderer.getGameObject().transform;
        Matrix4d mat = new Matrix4d(transform.getMatrix());
        mat.translate(spriteRenderer.offset);
        Vector4d[] positions = new Vector4d[] {
                new Vector4d(-0.5, 0.5, 0, 1),
                new Vector4d(0.5, 0.5, 0, 1),
                new Vector4d(0.5, -0.5, 0, 1),
                new Vector4d(-0.5, -0.5, 0, 1)
        };
        float[] vertices = new float[POS_SIZE * positions.length];
        // Set the position of the sprite.
        for (int i = 0; i < positions.length; ++i) {
            positions[i].x *= spriteRenderer.getSprite().getWidth();
            positions[i].y *= spriteRenderer.getSprite().getHeight();
            positions[i].y *= zIndex == -1 ? 1 : -1;
            positions[i].rotateZ(spriteRenderer.angle);
            positions[i].mul(mat);
            vertices[i * POS_SIZE] = (float)positions[i].x;
            vertices[i * POS_SIZE + 1] = (float)positions[i].y;
            vertices[i * POS_SIZE + 2] = (float)positions[i].z;
        }

        return vertices;
    }
}
