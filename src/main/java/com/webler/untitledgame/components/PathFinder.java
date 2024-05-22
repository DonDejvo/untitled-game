package com.webler.untitledgame.components;

import com.webler.goliath.algorithm.Dijkstra;
import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.core.Component;
import com.webler.untitledgame.level.levelmap.Platform;
import lombok.Getter;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.Arrays;

public class PathFinder extends Component {
    private final Level level;
    @Getter
    private Vertex[] path;
    private boolean isSamePlatform;

    public PathFinder(Level level) {
        this.level = level;
        path = null;
        isSamePlatform = false;
    }

    /**
    * Calculates and returns the path from the current position to the target. This is used to determine if the player is moving to a different position in the world
    * 
    * @param targetPos - The position to calculate
    */
    public void calculatePath(Vector3d targetPos) {
        Dijkstra dijkstra = level.getDijkstra();

        Vector3i sourceBlockCoords = level.getBlockCoords(gameObject.transform.position);
        Platform sourceBlockPlatform = level.getBlockPlatform(sourceBlockCoords.x, sourceBlockCoords.z);
        Vector3i targetBlockCoords = level.getBlockCoords(targetPos);
        Platform targetBlockPlatform = level.getBlockPlatform(targetBlockCoords.x, targetBlockCoords.z);

        // Returns the shortest path to the shortest path between two blocks.
        if(sourceBlockPlatform == targetBlockPlatform) {
            isSamePlatform = true;
            path = null;

        } else {
            isSamePlatform = false;

            int source = getClosestBlockId(sourceBlockCoords, sourceBlockPlatform);
            int target = getClosestBlockId(targetBlockCoords, targetBlockPlatform);

            // Returns the shortest path to the shortest path to the target.
            if(source == -1 || target == -1) {
                path = null;
            } else {
                dijkstra.reset();
                dijkstra.computePath(source);
                path = dijkstra.getShortestPathTo(target);

                // Find the vertex paths in the path.
                if(path != null) {
                    // Find the first vertex in the path.
                    while(path.length > 1) {
                        Platform platform = getVertexPlatform(path[0]);
                        // Check if the block platform is the same as sourceBlockPlatform.
                        if(!platform.equals(sourceBlockPlatform)) {
                            break;
                        }
                        path = Arrays.copyOfRange(path, 1, path.length);
                    }
                }
            }
        }
    }

    /**
    * Returns true if this Platform is the same as the one used to create the platform. This is determined by looking at the platform property of the Platform object.
    * 
    * 
    * @return whether this Platform is the same as the one used to create the platform or not ( false if not
    */
    public boolean isSamePlatform() {
        return isSamePlatform;
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the progress bar. This is called every frame to indicate the progress of the animation. The time in seconds since the last call to update () is given by dt
    * 
    * @param dt - the time since the last
    */
    @Override
    public void update(double dt) {

    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Gets the platform of the vertex. This is used to determine where the vertex should be drawn in the level
    * 
    * @param vertex - The vertex to look for
    * 
    * @return The platform of the vertex or null if none could be found in the level's block of the
    */
    private Platform getVertexPlatform(Vertex vertex) {
        int levelWidth = level.getLevelMap().getWidth();

        int coordY = vertex.getId() / levelWidth;
        int coordX = vertex.getId() % levelWidth;

        return level.getBlockPlatform(coordX, coordY);
    }

    /**
    * Returns the id of the closest block to the given coordinates. This is used to find the closest block to a given platform
    * 
    * @param blockCoords - Coordinates of the block to find the closest id for
    * @param platform - The platform to find the closest id for.
    * 
    * @return The id of the closest block to the given coordinates or - 1 if none found ( in which case the id is not returned
    */
    private int getClosestBlockId(Vector3i blockCoords, Platform platform) {
        Dijkstra dijkstra = level.getDijkstra();
        int levelWidth = level.getLevelMap().getWidth();

        int id = -1;
        double minDistance = 1000000;

        for(Vertex vertex : dijkstra.getVertices()) {
            int coordY = vertex.getId() / levelWidth;
            int coordX = vertex.getId() % levelWidth;

            // Check if the block is a block platform.
            if(level.getBlockPlatform(coordX, coordY) != platform) continue;

            double d = Math.sqrt(Math.pow(blockCoords.x - coordX, 2) + Math.pow(blockCoords.z - coordY, 2));
            // Set the distance of the vertex to the minimum distance.
            if(d < minDistance) {
                minDistance = d;
                id = vertex.getId();
            }
        }

        return id;
    }
}
