package com.webler.untitledgame.components;

import com.webler.goliath.algorithm.Dijkstra;
import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.core.Component;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class PathFinder extends Component {
    private Level level;
    private Vertex[] path;

    public PathFinder(Level level) {
        this.level = level;
        path = null;
    }

    public void calculatePath(Vector3d targetPos) {
        Dijkstra dijkstra = level.getDijkstra();

        Vector3i sourceBlockCoords = level.getBlockCoords(gameObject.transform.position);
        Vector3i targetBlockCoords = level.getBlockCoords(targetPos);

        int levelWidth = level.getLevelMap().getWidth();
        int source = sourceBlockCoords.z * levelWidth + sourceBlockCoords.x;
        int target = targetBlockCoords.z * levelWidth + targetBlockCoords.x;

        dijkstra.reset();
        dijkstra.computePath(source);
        path = dijkstra.getShortestPathTo(target);
    }

    public Vertex[] getPath() {
        return path;
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
}
