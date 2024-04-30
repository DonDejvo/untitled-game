package com.webler.goliath.graphics;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.Rect;
import com.webler.goliath.utils.AssetPool;
import org.joml.Matrix4d;
import org.joml.Vector3d;

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
    private final int zIndex;

    public SpriteBatch(int zIndex) {
        this.zIndex = zIndex;
        spriteRenderers = new ArrayList<>();
        drawCalls = new ArrayList<>();
    }

    public void add(SpriteRenderer spriteRenderer) {
        spriteRenderers.add(spriteRenderer);
    }

    public boolean remove(SpriteRenderer spriteRenderer) {
        return spriteRenderers.remove(spriteRenderer);
    }

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
        for (int i = 0; i < MAX_SPRITES; ++i) {
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
    public void initBuffers() {
        drawCalls.clear();

        if(spriteRenderers.isEmpty()) return;

        ArrayList<SpriteRenderer> visibleRenderers = spriteRenderers.stream()
                .sorted(Comparator.comparingInt(a -> a.getSprite().getTexture().getTexId()))
                .collect(Collectors.toCollection(ArrayList::new));

        float[] vertices = new float[visibleRenderers.size() * 4 * VERT_SIZE];

        for(int i = 0; i < visibleRenderers.size(); ++i) {
            SpriteRenderer spriteRenderer = visibleRenderers.get(i);
            Sprite sprite = spriteRenderer.getSprite();

            float[] positions = getPositions(spriteRenderer, sprite);
            float[] uvs = sprite.getTexCoords();
            float[] color = spriteRenderer.getColor().toArray();
            for(int j = 0; j < 4; ++j) {
                for(int k = 0; k < POS_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + POS_OFFSET + k] = positions[j * POS_SIZE + k];
                }
                for(int k = 0; k < UV_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + UV_OFFSET + k] = uvs[j * UV_SIZE + k];
                }
                System.arraycopy(color, 0, vertices, (i * 4 + j) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
            }

            if(i == visibleRenderers.size() - 1 || !sprite.getTexture().equals(visibleRenderers.get(i + 1).getSprite().getTexture())) {
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

    private static float[] getPositions(SpriteRenderer spriteRenderer, Sprite sprite) {
        Transform transform = spriteRenderer.getEntity().transform;
        Vector3d offsetPosition = spriteRenderer.getOffsetPosition();
        float x = (float)offsetPosition.x;
        float y = (float)offsetPosition.y;
        float z = (float)offsetPosition.z;
        float halfWidth = (float)(transform.scale.x * sprite.getWidth()) / 2;
        float halfHeight = (float)(transform.scale.y * sprite.getHeight()) / 2;

        return new float[] {
                x - halfWidth, y - halfHeight, z,
                x + halfWidth, y - halfHeight, z,
                x + halfWidth, y + halfHeight, z,
                x - halfWidth, y + halfHeight, z
        };
    }

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

    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    public int getzIndex() {
        return zIndex;
    }

    public boolean isFull() {
        return spriteRenderers.size() == MAX_SPRITES;
    }
}
