package com.webler.untitledgame.components;

import com.webler.goliath.algorithm.Dijkstra;
import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.core.Component;
import com.webler.untitledgame.level.levelmap.Platform;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.Arrays;

public class PathFinder extends Component {
    private Level level;
    private Vertex[] path;
    private boolean isSamePlatform;

    public PathFinder(Level level) {
        this.level = level;
        path = null;
        isSamePlatform = false;
    }

    public void calculatePath(Vector3d targetPos) {
        Dijkstra dijkstra = level.getDijkstra();

        Vector3i sourceBlockCoords = level.getBlockCoords(gameObject.transform.position);
        Platform sourceBlockPlatform = level.getBlockPlatform(sourceBlockCoords.x, sourceBlockCoords.z);
        Vector3i targetBlockCoords = level.getBlockCoords(targetPos);
        Platform targetBlockPlatform = level.getBlockPlatform(targetBlockCoords.x, targetBlockCoords.z);

        if(sourceBlockPlatform == targetBlockPlatform) {
            isSamePlatform = true;
            path = null;

        } else {
            isSamePlatform = false;

            int source = getClosestBlockId(sourceBlockCoords, sourceBlockPlatform);
            int target = getClosestBlockId(targetBlockCoords, targetBlockPlatform);

            if(source == -1 || target == -1) {
                path = null;
            } else {
                dijkstra.reset();
                dijkstra.computePath(source);
                path = dijkstra.getShortestPathTo(target);

                if(path != null) {
                    while(path.length > 1) {
                        Platform platform = getVertexPlatform(path[0]);
                        if(platform != sourceBlockPlatform) {
                            break;
                        }
                        path = Arrays.copyOfRange(path, 1, path.length);
                    }
                }
            }
        }
    }

    public Vertex[] getPath() {
        return path;
    }

    public boolean isSamePlatform() {
        return isSamePlatform;
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

    private Platform getVertexPlatform(Vertex vertex) {
        int levelWidth = level.getLevelMap().getWidth();

        int coordY = vertex.getId() / levelWidth;
        int coordX = vertex.getId() % levelWidth;

        return level.getBlockPlatform(coordX, coordY);
    }

    private int getClosestBlockId(Vector3i blockCoords, Platform platform) {
        Dijkstra dijkstra = level.getDijkstra();
        int levelWidth = level.getLevelMap().getWidth();

        int id = -1;
        double minDistance = 1000000;

        for(Vertex vertex : dijkstra.getVertices()) {
            int coordY = vertex.getId() / levelWidth;
            int coordX = vertex.getId() % levelWidth;

            if(level.getBlockPlatform(coordX, coordY) != platform) continue;

            double d = Math.sqrt(Math.pow(blockCoords.x - coordX, 2) + Math.pow(blockCoords.z - coordY, 2));
            if(d < minDistance) {
                minDistance = d;
                id = vertex.getId();
            }
        }

        return id;
    }
}
