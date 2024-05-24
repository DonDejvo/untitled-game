package com.webler.untitledgame.level.graphics;

import auburn.FastNoiseLite;
import com.webler.goliath.graphics.*;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Platform;
import org.joml.Vector3f;

import java.util.*;

public class LevelGeometry extends Geometry {
    private static final int VERT_SIZE = 8;
    private static final int[] indicesCache = new int[] {
            0, 1, 2,
            0, 2, 3
    };
    private float[] vertices;
    private int[] indices;
    private DrawCall[] drawCalls;
    private final Level level;
    private List<LevelGeometryQuad> quads;
    private Sprite[] groundSprites;
    private Sprite[] wallSprites;
    private Sprite[] ceilingSprites;
    private final FastNoiseLite noise;

    public LevelGeometry(Level level) {
        this.level = level;

        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

        init();
    }

    /**
    * Initializes the game. Called by Game#onCreate () after the game has been created
    */
    public void init() {
        LevelMap levelMap = level.getLevelMap();

        List<Platform> platforms = levelMap.getPlatforms();
        int globalCeiling = levelMap.getCeiling();

        Spritesheet spritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png");

        groundSprites = new Sprite[]{
                spritesheet.getSprite(11),
                spritesheet.getSprite(12),
                spritesheet.getSprite(20),
                spritesheet.getSprite(21),
                spritesheet.getSprite(22),
                //spritesheet.getSprite(23),
                spritesheet.getSprite(29),
                spritesheet.getSprite(30),
                spritesheet.getSprite(31),
                //spritesheet.getSprite(32),
        };
        wallSprites = new Sprite[]{
                spritesheet.getSprite(42),
                spritesheet.getSprite(51),
                spritesheet.getSprite(43),
                //spritesheet.getSprite(52),
                spritesheet.getSprite(44),
                spritesheet.getSprite(53),
        };
        ceilingSprites = new Sprite[]{
                spritesheet.getSprite(42)
        };

        quads = new ArrayList<>();

        //addCeiling(levelMap.getWidth(), levelMap.getHeight(), globalCeiling);

        for(Platform platform : platforms) {
            float x1 = (platform.getX() - levelMap.getMinX());
            float x2 = (platform.getX() - levelMap.getMinX() + platform.getWidth());
            float y1 = (platform.getY() - levelMap.getMinY());
            float y2 = (platform.getY() - levelMap.getMinY() + platform.getHeight());
            float top = (float) platform.getTop();
            float ceiling = (float) platform.getCeiling();

            // Add a round to the top of the line.
            if(top < globalCeiling) {
                addGround(x1, y1, x2, y2, top);
            }
            // Add walls to the top wall.
            if(top > 0) {
                addWall(x1, y1, x1, y2, 0, top);
                addWall(x1, y2, x2, y2, 0, top);
                addWall(x2, y2, x2, y1, 0, top);
                addWall(x2, y1, x1, y1, 0, top);
            }
            // Add ceiling to the ceiling.
            if(ceiling <= globalCeiling) {
                addCeiling(x1, y1, x2, y2, ceiling);
            }
            // Add walls to the walls.
            if(ceiling < globalCeiling) {
                addWall(x1, y1, x1, y2, ceiling, globalCeiling);
                addWall(x1, y2, x2, y2, ceiling, globalCeiling);
                addWall(x2, y2, x2, y1, ceiling, globalCeiling);
                addWall(x2, y1, x1, y1, ceiling, globalCeiling);
            }
        }

        // This method is used to add walls to the map.
        for (int y = 0; y < levelMap.getHeight(); y++) {
            // This method is used to add walls to the map.
            for (int x = 0; x < levelMap.getWidth(); x++) {
                // Returns true if the block is at the top of the block.
                if(level.getBlockTop(x,y) < 0) {
                    continue;
                }
                // Add a wall to the top of the block.
                if(y == 0 || level.getBlockTop(x,y - 1) < 0) {
                    addWall(x, y, (x + 1), y, 0, globalCeiling);
                }
                // Add a wall to the map.
                if(y == levelMap.getHeight() - 1 || level.getBlockTop(x,y + 1) < 0) {
                    addWall((x + 1), (y + 1), x, (y + 1), 0, globalCeiling);
                }
                // Add a wall to the wall.
                if(x == 0 || level.getBlockTop(x - 1,y) < 0) {
                    addWall(x, (y + 1), x, y, 0, globalCeiling);
                }
                // Add a wall to the map.
                if(x == levelMap.getWidth() - 1 || level.getBlockTop(x + 1,y) < 0) {
                    addWall((x + 1), y, (x + 1), (y + 1), 0, globalCeiling);
                }
            }
        }

        quads.sort(Comparator.comparingInt(LevelGeometryQuad::texId));

        int vertexCount = quads.size() * 4 * VERT_SIZE;
        List<DrawCall> drawCallList = new ArrayList<>();

        vertices = new float[vertexCount];
        indices = new int[quads.size() * 6];

        // Draw all quads in the scene.
        for(int i = 0; i < quads.size(); ++i) {
            LevelGeometryQuad quad = quads.get(i);
            float[] positions = quad.positions();
            float[] uvs = quad.uvs();
            Vector3f v1 = new Vector3f(positions[0], positions[1], positions[2]);
            Vector3f v2 = new Vector3f(positions[3], positions[4], positions[5]);
            Vector3f v3 = new Vector3f(positions[6], positions[7], positions[8]);
            Vector3f normal = new Vector3f(v1).sub(v2).cross(new Vector3f(v3).sub(v2)).normalize();
            float[] n = new float[] { normal.x, normal.y, normal.z };
            // The vertices are the vertices of the tiling.
            for (int j = 0; j < 4; ++j) {
                // Set the vertices and positions of the tile.
                for(int k = 0; k < 3; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + k] = positions[j * 3 + k] * Level.TILE_SIZE;
                }
                // Set the vertices of the vertex.
                for(int k = 0; k < 2; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + 3 + k] = uvs[j * 2 + k];
                }
                // Set the vertices of the triangle.
                for(int k = 0; k < 3; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + 5 + k] = n[k];
                }
            }
            // Set indices for all 6 indices in the cache
            for(int j = 0; j < 6; ++j) {
                indices[i * 6 + j] = indicesCache[j] + i * 4;
            }

            // Add a draw call to the draw call list.
            if(i == quads.size() - 1 || quad.texId() != quads.get(i + 1).texId()) {
                // Add a DrawCall to the draw call list.
                if(drawCallList.isEmpty()) {
                    drawCallList.add(new DrawCall(0, (i + 1) * 6, quad.texId()));
                } else {
                    DrawCall prevDrawCall = drawCallList.get(drawCallList.size() - 1);
                    int offset = prevDrawCall.offset() + prevDrawCall.count();
                    int count = (i + 1) * 6 - offset;
                    drawCallList.add(new DrawCall(offset, count, quad.texId()));
                }

            }
        }

        drawCalls = new DrawCall[drawCallList.size()];
        // This method is used to set the drawCalls array of all the drawCalls in the drawCallList.
        for (int i = 0; i < drawCallList.size(); i++) {
            drawCalls[i] = drawCallList.get(i);
        }
    }

    private void addGround(float x1, float y1, float x2, float y2, float top) {

        // Adds a LevelGeometryQuad to the ground sprites.
        for (float x = x1; x < x2; ++x) {
            // Adds a LevelGeometryQuad to the ground sprites.
            for (float y = y1; y < y2; ++y) {
                float[] positions = new float[]{
                        x, top, y,
                        x + 1, top, y,
                        x + 1, top, y + 1,
                        x, top, y + 1
                };
                float value = noise.GetNoise(x * 100, y * 100) * 0.5f + 0.5f;
                Sprite sprite = groundSprites[(int)Math.floor(value * groundSprites.length)];
                quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
            }
        }
    }

    private void addWall(float x1, float y1, float x2, float y2, float bottom, float top) {
        // The method returns a list of LevelGeometryQuad objects.
        if(x1 == x2) {
            // Creates a LevelGeometryQuad objects for the given points.
            if(y1 < y2) {
                // Creates a LevelGeometryQuad for each level of the game.
                for (float y = y1; y < y2; ++y) {
                    // Adds a LevelGeometryQuad to the LevelGeometryQuads.
                    for(float i = bottom; i < top; ++i) {
                        float[] positions = new float[]{
                                x1, i + 1, y,
                                x1, i + 1, y + 1,
                                x1, i, y + 1,
                                x1, i, y
                        };
                        float value = noise.GetNoise(y * 100, i * 100) * 0.5f + 0.5f;
                        Sprite sprite = wallSprites[(int)Math.floor(value * wallSprites.length)];
                        quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
                    }
                }
            } else {
                // Creates a LevelGeometryQuad for each level of the game.
                for (float y = y1; y > y2; --y) {
                    // Adds a LevelGeometryQuad to the LevelGeometryQuads.
                    for(float i = bottom; i < top; ++i) {
                        float[] positions = new float[]{
                                x1, i + 1, y,
                                x1, i + 1, y - 1,
                                x1, i, y - 1,
                                x1, i, y
                        };
                        float value = noise.GetNoise(y * 100, i * 100) * 0.5f + 0.5f;
                        Sprite sprite = wallSprites[(int)Math.floor(value * wallSprites.length)];
                        quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
                    }
                }
            }
        } else {
            // The method to create a level geometry quads.
            if(x1 < x2) {
                // Creates a LevelGeometryQuad for each level of the game.
                for (float x = x1; x < x2; ++x) {
                    // Adds a LevelGeometryQuad to the LevelGeometryQuads.
                    for(float i = bottom; i < top; ++i) {
                        float[] positions = new float[]{
                                x, i + 1, y1,
                                x + 1, i + 1, y1,
                                x + 1, i, y1,
                                x, i, y1
                        };
                        float value = noise.GetNoise(x * 100, i * 100) * 0.5f + 0.5f;
                        Sprite sprite = wallSprites[(int)Math.floor(value * wallSprites.length)];
                        quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
                    }
                }
            } else {
                // Creates a LevelGeometryQuad for each level of the game.
                for (float x = x1; x > x2; --x) {
                    // Adds a LevelGeometryQuad to the LevelGeometryQuads.
                    for(float i = bottom; i < top; ++i) {
                        float[] positions = new float[]{
                                x, i + 1, y1,
                                x - 1, i + 1, y1,
                                x - 1, i, y1,
                                x, i, y1
                        };
                        float value = noise.GetNoise(x * 100, i * 100) * 0.5f + 0.5f;
                        Sprite sprite = wallSprites[(int)Math.floor(value * wallSprites.length)];
                        quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
                    }
                }
            }
        }
    }

    private void addCeiling(float x1, float y1, float x2, float y2, float top) {
        // Adds a LevelGeometryQuad to the ceilingSprites.
        for (float x = x1; x < x2; ++x) {
            // Adds a LevelGeometryQuad to the ceilingSprites.
            for (float y = y1; y < y2; ++y) {
                float[] positions = new float[]{
                        x, top, y + 1,
                        x + 1, top, y + 1,
                        x + 1, top, y,
                        x, top, y
                };
                Sprite sprite = ceilingSprites[0];
                quads.add(new LevelGeometryQuad(positions, sprite.getTexCoords(), sprite.getTexture().getTexId()));
            }
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

        private record LevelGeometryQuad(float[] positions, float[] uvs, int texId) {

    }
}
