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
    private static final int MAX_QUADS = 10000;
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
    private final Game game;
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
    private final Matrix4d projection;
    @Getter
    private Vector2f translate;
    private Stack<Vector2f> translateStack;
    private List<DrawCall> drawCalls;
    private final List<CanvasQuad> quads;

    public Canvas(Game game) {
        this.game = game;
        vertices = new float[VERT_SIZE * 4 * MAX_QUADS];
        projection = new Matrix4d();
        quads = new ArrayList<>();
    }

    /**
    * Initializes the renderer. Must be called before rendering a frame to ensure it is ready to receive data from
    */
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
        // Sets the indices of the indices in the cache.
        for (int i = 0; i < MAX_QUADS; ++i) {
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

    /**
    * Push a translation to the stack. This translates the text by the specified amount in the x and y direction
    * 
    * @param x - the amount to translate in the x direction
    * @param y - the amount to translate in the y direction ( must be greater than 0
    */
    public void pushTranslate(float x, float y) {
        Vector2f vec = new Vector2f(x, y);
        translateStack.push(vec);
        this.translate.add(vec);
    }

    /**
    * Pops and subtracts the last translated point from the stack. This is useful for translating a shape
    */
    public void popTranslate() {
        Vector2f vec = translateStack.pop();
        this.translate.sub(vec);
    }

    /**
    * Resets the translation stack to the initial state. This is useful when reusing a TextMorphism object
    */
    public void resetTranslate() {
        translateStack.clear();
        translate.set(0, 0);
    }

    /**
    * Clears draw calls and quads. Call this before drawing a new frame to ensure that the frame is in a consistent state
    */
    public void beginFrame() {
        drawCalls.clear();
        quads.clear();
    }

    /**
    * Ends the frame. This is called at the end of each frame to update the vertex positions uvs
    */
    public void endFrame() {

        // Draws all the CanvasQuads in the canvas.
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
            // Copy all the vertices and uvs from the vertices and color data.
            for(int j = 0; j < 4; ++j) {
                // Set the position of the vertices in the graph.
                for(int k = 0; k < POS_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + POS_OFFSET + k] = positions[j * POS_SIZE + k];
                }
                // Set the vertices of the vertex.
                for(int k = 0; k < UV_SIZE; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + UV_OFFSET + k] = uvs[j * UV_SIZE + k];
                }
                System.arraycopy(colorData, 0, vertices, (i * 4 + j) * VERT_SIZE + COLOR_OFFSET, COLOR_SIZE);
            }
            // Add a draw call to the drawCalls list.
            if(i == quads.size() - 1 || quad.texId != quads.get(i + 1).texId) {
                // Add draw calls to the draw calls list.
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

    /**
    * Draws a rectangle on the screen. The rectangle is drawn with the current color as the outline color.
    * 
    * @param x - the x coordinate of the upper - left corner of the rectangle
    * @param y - the y coordinate of the upper - left corner of the rectangle
    * @param w - the width of the rectangle. This is the distance from the left edge of the screen to the width of the rectangle.
    * @param h - the height of the rectangle. This is the distance from the top edge of the screen to the height
    */
    public void rect(float x, float y, float w, float h) {
        drawQuad(0, 0, 0, 0, 0, x, y, w, h, color);
    }

    /**
    * Draws an image on the screen. The image is drawn using the current color as the fill color.
    * 
    * @param texId - Specifies the texture id to use.
    * @param sx0 - Specifies the X coordinate of the upper - left corner of the rectangle.
    * @param sy0 - Specifies the Y coordinate of the upper - left corner of the rectangle
    * @param sx1
    * @param sy1
    * @param x
    * @param y
    * @param w
    * @param h
    * @param color
    */
    public void image(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h, Color color) {
        drawQuad(texId, sx0, sy0, sx1, sy1, x, y, w, h, color);
    }

    /**
    * Draw an image. The image is drawn with the specified texture coordinates and size. Note that the width and height are in tex coords not tex coords + w * h
    * 
    * @param texId - Specifies the texture id to use.
    * @param sx0 - Specifies the X coordinate of the upper - left corner of the image.
    * @param sy0 - Specifies the Y coordinate of the upper - left corner of the image
    * @param sx1
    * @param sy1
    * @param x
    * @param y
    * @param w
    * @param h
    */
    public void image(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h) {
        image(texId, sx0, sy0, sx1, sy1, x, y, w, h, Color.WHITE);
    }

    /**
    * Draw an image. The image is drawn at the specified location with the specified width and height. Note that pixels outside the image are ignored.
    * 
    * @param texId - Texture ID to draw to. Must be 1 to 4 in order to use this method.
    * @param x - X coordinate of the upper - left corner of the image.
    * @param y - Y coordinate of the upper - left corner of the image
    * @param w
    * @param h
    */
    public void image(int texId, float x, float y, float w, float h) {
        image(texId, 0, 0, 1, 1, x, y, w, h);
    }

    /**
    * x The x coordinate of the upper left corner of the
    * 
    * @param x
    * @param y
    */
    public void text(String text, float x, float y) {
        Spritesheet spritesheet = bitmapFont.spritesheet();
        float charWidth = fontSize * spritesheet.getSpriteWidth() / spritesheet.getSpriteHeight();
        float charHeight = fontSize;
        float offsetX = 0;
        // The text alignment is the text alignment.
        float alignOffsetX = switch (textAlign) {
            case CENTER -> -charWidth * text.length() * 0.5f;
            case RIGHT -> -charWidth * text.length();
            case LEFT -> 0;
        };
        // Draw all the text in the bitmap font.
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            Sprite sprite = bitmapFont.getCharSprite(c);
            // Draws the sprite to the image.
            if(sprite != null) {
                float[] uvs = sprite.getTexCoords();
                image(sprite.getTexture().getTexId(),
                        uvs[0], uvs[1], uvs[4], uvs[5],
                        x + offsetX + alignOffsetX, y, charWidth, charHeight, color);
            }
            offsetX += charWidth;
        }
    }

    /**
    * Returns the width of the game. This is used to determine how much space is available to draw the game.
    * 
    * 
    * @return int The width of the game in pixels or 0 if there is no width to draw at all ( in this case the width can be determined by the game
    */
    public int getWidth() {
        return game.getWidth();
    }

    /**
    * Returns the height of the game. This is used to determine how much space is available to draw the game.
    * 
    * 
    * @return int The height of the game in pixels or 0 if there is no height to draw at all ( in this case the height can be calculated from the game
    */
    public int getHeight() {
        return game.getHeight();
    }

    /**
    * Computes the width of the text. This is based on the text length divided by the font size.
    * 
    * @param text - The text to compute the width of. Must not be null.
    * 
    * @return The width of the text in pixels. May be 0 if the text is empty or not a font
    */
    public float computeTextWidth(String text) {
        Spritesheet spritesheet = bitmapFont.spritesheet();
        return text.length() * fontSize * spritesheet.getSpriteWidth() / spritesheet.getSpriteHeight();
    }

    /**
    * Clears the OpenGL state. This is useful for debugging and to ensure that you don't accidentally destroy the object
    */
    public void clear() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

    /**
    * Draws a quad on the canvas. This is the method that does not take into account texture coordinates.
    * 
    * @param texId - Texture to use for drawing. Must be one of #TEXTURE_GRADIENT #TEXTURE_FLOOR or #TEXTURE_BOTH.
    * @param sx0 - X coordinate of the upper - left corner of the quad.
    * @param sy0 - Y coordinate of the upper - left corner of the quad.
    * @param sx1 - X coordinate of the lower - right corner of the quad.
    * @param sy1 - Y coordinate of the lower - right corner of the quad.
    * @param x - X coordinate of the upper - left corner of the quad.
    * @param y - Y coordinate of the upper - left corner of the quad.
    * @param w - Width of the rectangle to draw. If w is negative it is interpreted as the width of the area being drawn.
    * @param h - Height of the rectangle to draw. If h is negative it is interpreted as the height of the area being drawn.
    * @param color - Color of the quad to draw. If null the color is taken from Graphics#getColor ()
    */
    private void drawQuad(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h, Color color) {
        // Returns true if there are more quads than MAX_QUADS.
        if(quads.size() == MAX_QUADS) {
            return;
        }
        quads.add(new CanvasQuad(texId, sx0, sy0, sx1, sy1, translate.x + x, translate.y + y, w, h, color));
    }

    private record CanvasQuad(int texId, float sx0, float sy0, float sx1, float sy1, float x, float y, float w, float h,
                              Color color) {
    }
}
