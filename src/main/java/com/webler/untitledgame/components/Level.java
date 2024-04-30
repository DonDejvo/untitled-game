package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Platform;
import org.joml.Vector3d;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Level extends Component {
    public static final int TILE_SIZE = 4;
    private final LevelMap levelMap;
    private GridItem[][] grid;
    private String path;

    public Level() {
        this.levelMap = new LevelMap();
        path = null;
        buildGrid();
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }

    public LevelMap getLevelMap() {
        return levelMap;
    }

    public void load(String fileName) {
        try {
            levelMap.clear();
            levelMap.load(fileName);
            path = fileName;
            buildGrid();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void save(String fileName) {
        try {
            levelMap.save(fileName);
            path = fileName;
        } catch (TransformerException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public int getBlockTop(int x, int y) {
        return grid[y][x].top;
    }

    public boolean isBlockAt(int x, int y, int z) {
        if(x < 0 || z < 0 || z >= grid.length || x >= grid[0].length) {
            return true;
        }
        GridItem gridItem = grid[z][x];
        return gridItem.top == -1 || y < gridItem.top || y >= gridItem.ceiling;
    }

    public boolean isBlockAtBox(Vector3d min, Vector3d max) {
        int blockMinX = (int)Math.floor(min.x / TILE_SIZE);
        int blockMinZ = (int)Math.floor(min.z / TILE_SIZE);
        int blockMaxX = (int)Math.floor(max.x / TILE_SIZE);
        int blockMaxZ = (int)Math.floor(max.z / TILE_SIZE);
        int blockMinY = (int) Math.floor(min.y / TILE_SIZE);
        int blockMaxY = (int) Math.floor(max.y / TILE_SIZE);
        for(int z = blockMinZ; z <= blockMaxZ; ++z) {
            for(int x = blockMinX; x <= blockMaxX; ++x) {
                for(int y = blockMinY; y <= blockMaxY; ++y) {
                    if(isBlockAt(x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void buildGrid() {
        int mapWidth = levelMap.getWidth();
        int mapHeight = levelMap.getHeight();
        grid = new GridItem[mapHeight][mapWidth];
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                grid[i][j] = new GridItem(-1, levelMap.ceiling);
            }
        }
        for(Platform platform : levelMap.getPlatforms()) {
            int x1, x2, y1, y2;
            x1 = platform.x - levelMap.minX;
            x2 = x1 + platform.width - 1;
            y1 = platform.y - levelMap.minY;
            y2 = y1 + platform.height- 1;

            for(int y = y1; y <= y2; ++y) {
                for(int x = x1; x <= x2; ++x) {
                    GridItem gridItem = grid[y][x];
                    if(gridItem.top <= platform.top) {
                        gridItem.top = platform.top;
                    }
                    if(gridItem.ceiling >= platform.ceiling) {
                        gridItem.ceiling = platform.ceiling;
                    }
                }
            }
        }
    }

    private static class GridItem {
        private int top, ceiling;

        public GridItem(int top, int ceiling) {
            this.top = top;
            this.ceiling = ceiling;
        }
    }
}
