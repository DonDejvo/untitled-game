package com.webler.goliath.graphics.canvas;

import com.webler.goliath.Game;
import com.webler.goliath.graphics.*;
import com.webler.goliath.graphics.font.BitmapFont;
import com.webler.goliath.utils.AssetPool;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4d;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Canvas {
    private static final int MAX_QUADS = 1000;
    private static final int POS_INDEX = 0;
    private static final int POS_SIZE = 2;
    private static final int POS_OFFSET = 0;
    private static final int UV_INDEX = 1;
    private static final int UV_SIZE = 2;
    private static final int UV_OFFSET = 2;
    private static final int COLOR_INDEX = 2;
    private static final int COLOR_SIZE = 4;
    private static final int COLOR_OFFSET = 4;
    private static final int VERT_SIZE = POS_SIZE + UV_SIZE + COLOR_SIZE;
    private Game game;
    private int vao;
    private int vbo;
    private int ebo;
    private final float[] vertices;
    private Shader shader;
    private Texture defaultTexture;
    @Setter
    private Color color;
    @Setter
    private BitmapFont bitmapFont;
    @Setter
    private float fontSize;
    @Setter
    @Getter
    private TextAlign textAlign;
    private Matrix4d projection;
    @Getter
    private Vector2f translate;
    private Stack<Vector2f> translateStack;
    private List<DrawCall> drawCalls;
    private List<CanvasQuad> quads;

    public Canvas(Game game) {
        this.game = game;
        vertices = new float[VERT_SIZE * 4 * MAX_QUADS];
        projection = new Matrix4d();
        quads = new ArrayList<>();
    }

    public void start() {
        color = Color.WHITE;
        textAlign = TextAlign.LEFT;
        translate = new Vector2f();
        translateStack = new Stack<>();
        drawCalls = new ArrayList<>();
        fontSize = 32;
        bitmapFont = AssetPool.getBitmapFont("default");
        shader = AssetPool.getShader("goliath/shaders/canvas.glsl");
        defaultTexture = AssetPool.getTexture("goliath/images/square.png");
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, MAX_QUADS * 4 * VERT_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
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
        int[] indices = new int[MAX_QUADS * 6];
        for (int i = 0; i < MAX_QUADS; ++i) {
            for(int j = 0; j < indexCache.length; ++j) {
                indices[i * 6 + j] = indexCache[j] + i * 4;
            }
        }

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public void pushTranslate(float x, float y) {
        Vector2f vec = new Vector2f(x, y);
        translateStack.push(vec);
        this.translate.add(vec);
    }

    public void popTranslate() {
        Vector2f vec = translateStack.pop();
        this.translate.sub(vec);
    }

    public void resetTranslate() {
        translateStack.clear();
        translate.set(0, 0);
    }

    public void beginFrame() {
        drawCalls.clear();
        quads.clear();
    }

    public void endFrame() {

        for(int i = 0; i < quads.size(); ++i) {
            CanvasQuad quad = quads.get(i);
            float[] positions = new float[] {
                    quad.x, quad.y,
                    quad.x + quad.w, quad.y,
                    quad.x + quad.w, quad.y + quad.h,
                    quad.x, quad.y + quad.h,
            };
            float[] uvs = new float[] {
                    quad.sx0, quad.sy0,
                    quad.sx1, quad.sy0,
                    quad.sx1, quad.sy1,
                    quad.sx0, quad.sy1,
            };
            float[] colorData = quad.color.toArray();
            for(int j = 0; j < 4; ++j) {
                for(int k = 0; k < POS_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + POS_OFFSET + k] = positions[j * POS_SIZE + k];
                }
                for(int k = 0; k < UV_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + UV_OFFSET + k] = uvs[j * UV_SIZE + k];
                }
                System.arraycopy(colorData, 0, vertices, (i * 4 + j) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
            }
            if(i == quads.size() - 1 || quad.texId != quads.get(i + 1).texId) {
                if(drawCalls.isEmpty()) {
                    drawCalls.add(new DrawCall(0, (i + 1) * 6, quad.texId));
                } else {
                    DrawCall prevDrawCall = drawCalls.get(drawCalls.size() - 1);
                    int offset = prevDrawCall.offset() + prevDrawCall.count();
                    int count = (i + 1) * 6 - offset;
                    drawCalls.add(new DrawCall(offset, count, quad.texId));
                }
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glViewport(0, 0, game.getWidth(), game.getHeight());
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glUseProgram(shader.getProgram());

        projection.identity().ortho2D(0, game.getWidth(), game.getHeight(), 0);
        shader.supplyUniform("u_projection", projection);

        glBindVertexArray(vao);

        glEnableVertexAttribArray(POS_INDEX);
        glEnableVertexAttribArray(UV_INDEX);
        glEnableVertexAttribArray(COLOR_INDEX);

        for (DrawCall drawCall : drawCalls) {
            glBindTexture(GL_TEXTURE_2D, drawCall.texId() == 0 ? defaultTexture.getTexId() : drawCall.texId());
            glDrawElements(GL_TRIANGLES, drawCall.count(), GL_UNSIGNED_INT, (long) drawCall.offset() * Integer.BYTES);
        }

        glDisableVertexAttribArray(POS_INDEX);
        glDisableVertexAttribArray(UV_INDEX);
        glDisableVertexAttribArray(COLOR_INDEX);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void rect(float x, float y, float w, float h) {
        drawQuad(0, 0, 0, 0, 0, x, y, w, h, color);
    }

    public void image(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h, Color color) {
        drawQuad(texId, sx0, sy0, sx1, sy1, x, y, w, h, color);
    }

    public void image(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h) {
        image(texId, sx0, sy0, sx1, sy1, x, y, w, h, Color.WHITE);
    }

    public void image(int texId, float x, float y, float w, float h) {
        image(texId, 0, 0, 1, 1, x, y, w, h);
    }

    public void text(String text, float x, float y) {
        Spritesheet spritesheet = bitmapFont.getSpritesheet();
        float charWidth = fontSize * spritesheet.getSpriteWidth() / spritesheet.getSpriteHeight();
        float charHeight = fontSize;
        float offsetX = 0;
        float alignOffsetX = switch (textAlign) {
            case CENTER -> -charWidth * text.length() * 0.5f;
            case RIGHT -> -charWidth * text.length();
            case LEFT -> 0;
        };
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Sprite sprite = bitmapFont.getCharSprite(c);
            if(sprite != null) {
                float[] uvs = sprite.getTexCoords();
                image(sprite.getTexture().getTexId(),
                        uvs[0], uvs[1], uvs[4], uvs[5],
                        x + offsetX + alignOffsetX, y, charWidth, charHeight, color);
            }
            offsetX += charWidth;
        }
    }

    public int getWidth() {
        return game.getWidth();
    }

    public int getHeight() {
        return game.getHeight();
    }

    public float computeTextWidth(String text) {
        Spritesheet spritesheet = bitmapFont.getSpritesheet();
        return text.length() * fontSize * spritesheet.getSpriteWidth() / spritesheet.getSpriteHeight();
    }

    public void clear() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    private void drawQuad(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h, Color color) {
        if(quads.size() == MAX_QUADS) {
            return;
        }
        quads.add(new CanvasQuad(texId, sx0, sy0, sx1, sy1, translate.x + x, translate.y + y, w, h, color));
    }

    private static class CanvasQuad {
        private int texId;
        private float sx0, sy0, sx1, sy1;
        private float x, y, w, h;
        private Color color;

        private CanvasQuad(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h, Color color) {
            this.texId = texId;
            this.sx0 = sx0;
            this.sy0 = sy0;
            this.sx1 = sx1;
            this.sy1 = sy1;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.color = color;
        }
    }
}
