package com.webler.untitledgame.level.geometry;

import com.webler.goliath.graphics.DrawCall;
import com.webler.goliath.graphics.Geometry;
import com.webler.untitledgame.components.Level;
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
    private final int groundTexId;
    private final int wallTexId;
    private final int ceilingTexId;

    public LevelGeometry(Level level, int groundTexId, int wallTexId, int ceilingTexId) {
        this.level = level;
        this.groundTexId = groundTexId;
        this.wallTexId = wallTexId;
        this.ceilingTexId = ceilingTexId;

        init();
    }

    public void init() {
        LevelMap levelMap = level.getLevelMap();

        List<Platform> platforms = levelMap.getPlatforms();
        int globalCeiling = levelMap.ceiling;

        quads = new ArrayList<>();

        //addCeiling(levelMap.getWidth(), levelMap.getHeight(), globalCeiling);

        for(Platform platform : platforms) {
            float x1 = (platform.x - levelMap.minX);
            float x2 = (platform.x - levelMap.minX + platform.width);
            float y1 = (platform.y - levelMap.minY);
            float y2 = (platform.y - levelMap.minY + platform.height);
            float top = (float) platform.top;
            float ceiling = (float) platform.ceiling;

            if(top < globalCeiling) {
                addGround(x1, y1, x2, y2, top);
            }
            if(top > 0) {
                addWall(x1, y1, x1, y2, 0, top);
                addWall(x1, y2, x2, y2, 0, top);
                addWall(x2, y2, x2, y1, 0, top);
                addWall(x2, y1, x1, y1, 0, top);
            }
            if(ceiling <= globalCeiling) {
                addCeiling(x1, y1, x2, y2, ceiling);
            }
            if(ceiling < globalCeiling) {
                addWall(x1, y1, x1, y2, ceiling, globalCeiling);
                addWall(x1, y2, x2, y2, ceiling, globalCeiling);
                addWall(x2, y2, x2, y1, ceiling, globalCeiling);
                addWall(x2, y1, x1, y1, ceiling, globalCeiling);
            }
        }

        for (int y = 0; y < levelMap.getHeight(); y++) {
            for (int x = 0; x < levelMap.getWidth(); x++) {
                if(level.getBlockTop(x,y) < 0) {
                    continue;
                }
                if(y == 0 || level.getBlockTop(x,y - 1) < 0) {
                    addWall(x, y, (x + 1), y, 0, globalCeiling);
                }
                if(y == levelMap.getHeight() - 1 || level.getBlockTop(x,y + 1) < 0) {
                    addWall((x + 1), (y + 1), x, (y + 1), 0, globalCeiling);
                }
                if(x == 0 || level.getBlockTop(x - 1,y) < 0) {
                    addWall(x, (y + 1), x, y, 0, globalCeiling);
                }
                if(x == levelMap.getWidth() - 1 || level.getBlockTop(x + 1,y) < 0) {
                    addWall((x + 1), y, (x + 1), (y + 1), 0, globalCeiling);
                }
            }
        }

        quads.sort(Comparator.comparingInt(LevelGeometryQuad::getTexId));

        int vertexCount = quads.size() * 4 * VERT_SIZE;
        List<DrawCall> drawCallList = new ArrayList<>();

        vertices = new float[vertexCount];
        indices = new int[quads.size() * 6];

        for(int i = 0; i < quads.size(); ++i) {
            LevelGeometryQuad quad = quads.get(i);
            float[] positions = quad.getPositions();
            float[] uvs = quad.getUvs();
            Vector3f v1 = new Vector3f(positions[0], positions[1], positions[2]);
            Vector3f v2 = new Vector3f(positions[3], positions[4], positions[5]);
            Vector3f v3 = new Vector3f(positions[6], positions[7], positions[8]);
            Vector3f normal = new Vector3f(v1).sub(v2).cross(new Vector3f(v3).sub(v2)).normalize();
            float[] n = new float[] { normal.x, normal.y, normal.z };
            for (int j = 0; j < 4; ++j) {
                for(int k = 0; k < 3; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + k] = positions[j * 3 + k] * Level.TILE_SIZE;
                }
                for(int k = 0; k < 2; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + 3 + k] = uvs[j * 2 + k];
                }
                for(int k = 0; k < 3; ++k) {
                    vertices[(i * 4 + j) * VERT_SIZE + 5 + k] = n[k];
                }
            }
            for(int j = 0; j < 6; ++j) {
                indices[i * 6 + j] = indicesCache[j] + i * 4;
            }

            if(i == quads.size() - 1 || quad.getTexId() != quads.get(i + 1).getTexId()) {
                if(drawCallList.isEmpty()) {
                    drawCallList.add(new DrawCall(0, (i + 1) * 6, quad.getTexId()));
                } else {
                    DrawCall prevDrawCall = drawCallList.get(drawCallList.size() - 1);
                    int offset = prevDrawCall.offset() + prevDrawCall.count();
                    int count = (i + 1) * 6 - offset;
                    drawCallList.add(new DrawCall(offset, count, quad.getTexId()));
                }

            }
        }

        drawCalls = new DrawCall[drawCallList.size()];
        for (int i = 0; i < drawCallList.size(); i++) {
            drawCalls[i] = drawCallList.get(i);
        }
    }

    private void addGround(float x1, float y1, float x2, float y2, float top) {
        float[] positions = new float[]{
                x1, top, y1,
                x2, top, y1,
                x2, top, y2,
                x1, top, y2
        };
        quads.add(new LevelGeometryQuad(positions, groundTexId, Math.abs(x2 - x1), Math.abs(y2 - y1)));
    }

    private void addWall(float x1, float y1, float x2, float y2, float bottom, float top) {
        float[] positions = new float[]{
                x1, top, y1,
                x2, top, y2,
                x2, bottom, y2,
                x1, bottom, y1
        };
        quads.add(new LevelGeometryQuad(positions, wallTexId, Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1)), Math.max(top - bottom, 0)));
    }

    private void addCeiling(float x1, float y1, float x2, float y2, float top) {
        float[] positions = new float[]{
                x1, top, y2,
                x2, top, y2,
                x2, top, y1,
                x1, top, y1
        };
        quads.add(new LevelGeometryQuad(positions, ceilingTexId, Math.abs(x2 - x1), Math.abs(y2 - y1)));
    }

    private void addCeiling(float width, float height, float top) {
        float[] positions = new float[]{
                0, top, height,
                width, top, height,
                width, top, 0,
                0, top, 0
        };
        quads.add(new LevelGeometryQuad(positions, ceilingTexId, width, height));
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

    private class LevelGeometryQuad {
        private final float[] positions;
        private final int texId;
        private final float width;
        private final float height;
        private final float[] uvs;

        public LevelGeometryQuad(float[] positions, int texId, float width, float height) {
            this.positions = positions;
            this.texId = texId;
            this.width = width;
            this.height = height;
            uvs = new float[] {
                    0, 0,
                    width, 0,
                    width, height,
                    0, height
            };
        }

        public float[] getPositions() {
            return positions;
        }

        public int getTexId() {
            return texId;
        }

        public float[] getUvs() {
            return uvs;
        }

        public float getHeight() {
            return height;
        }

        public float getWidth() {
            return width;
        }
    }
}
