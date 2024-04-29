package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Platform;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Level extends Component {
    public static final int TILE_SIZE = 2;
    private final LevelMap levelMap;
    private int[][] grid;
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

    public int getTileTop(double x, double y) {
        int tileX = (int) Math.floor(x / TILE_SIZE);
        int tileY = (int) Math.floor(y / TILE_SIZE);
        if(tileX < 0 || tileY < 0 || tileY >= grid.length || tileX >= grid[0].length) {
            return 0;
        }
        return grid[tileY][tileX];
    }

    public int[][] getGrid() {
        return grid;
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

    private void buildGrid() {
        int mapWidth = levelMap.getWidth();
        int mapHeight = levelMap.getHeight();
        grid = new int[mapHeight][mapWidth];
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                grid[i][j] = -1;
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
                    if(grid[y][x] < platform.top) {
                        grid[y][x] = platform.top;
                    }
                }
            }
        }
    }
}
