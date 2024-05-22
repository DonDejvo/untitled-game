package com.webler.goliath.algorithm;

import lombok.Getter;

public class Dijkstra {
    private static final int MAX_DISTANCE = 1000000;
    @Getter
    private Vertex[] vertices;
    @Getter
    private Edge[] edges;
    private VertexData[] vertexData;
    private int[][] edgeGrid;

    /**
    * Builds the graph. This is the method that will be called by the constructor. If you don't want to call this yourself it is recommended to call buildGraph ( vertices edges )
    * 
    * @param vertices - an array of vertices that are the source of the edge
    * @param edges - an array of edges that are the target of the edge
    * @param directed - true if the graph is directed false if it is
    */
    public void buildGraph(Vertex[] vertices, Edge[] edges, boolean directed) {
        this.vertices = vertices;
        this.edges = edges;
        edgeGrid = new int[vertices.length][vertices.length];
        // Set the distance between the vertices and edges.
        for (int i = 0; i < vertices.length; i++) {
            // Set the distance between the vertices and edges.
            for (int j = 0; j < vertices.length; j++) {
                edgeGrid[i][j] = i == j ? 0 : MAX_DISTANCE;
            }
        }
        vertexData = new VertexData[vertices.length];
        // Creates a new vertex data array.
        for (int i = 0; i < vertices.length; i++) {
            vertexData[i] = new VertexData(vertices[i]);
        }
        for (Edge edge : edges) {
            int source = edge.getSource();
            int target = edge.getTarget();
            int sourceIdx = -1, targetIdx = -1;
            // Find the vertex that is in the source vertexData array.
            for (int i = 0; i < vertexData.length; i++) {
                // Set the source vertex to the source vertex
                if(vertexData[i].vertex.getId() == source) {
                    sourceIdx = i;
                }
                // Find the target vertex in the vertexData array
                if(vertexData[i].vertex.getId() == target) {
                    targetIdx = i;
                }
            }
            // Set edge weight to weight
            if(sourceIdx != -1 && targetIdx != -1) {
                edgeGrid[sourceIdx][targetIdx] = edge.getWeight();
                // Set the weight of the edge.
                if (!directed) {
                    edgeGrid[targetIdx][sourceIdx] = edge.getWeight();
                }
            }
        }
    }

    /**
    * Computes the shortest path from source to all vertices. This is a non - recursive method. It does not return until there is a path to the source.
    * 
    * @param source - Id of the source vertex to compute the shortest path
    */
    public void computePath(int source) {
        int visitedCount = 0;
        int sourceVertexIdx = -1;
        // Find the source vertex in the source vertexData.
        for (int i = 0; i < vertexData.length; i++) {
            // Set the vertex index to the source vertex
            if(vertexData[i].vertex.getId() == source) {
                sourceVertexIdx = i;
            }
        }

        // Returns true if the source vertex is in the source vertex list.
        if(sourceVertexIdx == -1) return;

        vertexData[sourceVertexIdx].minDistance = 0;
        vertexData[sourceVertexIdx].order = 0;

        // Find the nearest vertex in the graph.
        while (visitedCount < vertexData.length) {
            int currentIdx = visitNearestVertex();

            // If the current index is not in the current position break.
            if(currentIdx == -1) break;

            VertexData currentVertex = vertexData[currentIdx];
            // Find the distance between the vertex data and the current vertex.
            for(int i = 0; i < edgeGrid[currentIdx].length; ++i) {
                VertexData neighbor = vertexData[i];
                int distance = currentVertex.minDistance + edgeGrid[currentIdx][i];
                // Set the distance between the current vertex and the current vertex.
                if(distance < neighbor.minDistance) {
                    neighbor.minDistance = distance;
                    neighbor.order = currentVertex.order + 1;
                    neighbor.prev = currentVertex;
                }
            }
            ++visitedCount;
        }
    }

    /**
    * Returns the distance between the vertex and the target. This is used to determine the shortest path in the graph to get to a vertex that is closer to the target than it would have if it were added to the graph.
    * 
    * @param target - The index of the vertex to get the distance to.
    * 
    * @return The distance between the vertex and the target or - 1 if there is no such vertex in the graph
    */
    public int getDistance(int target) {
        return vertexData[target].minDistance;
    }

    /**
    * Returns the shortest path from the vertex with id target to the first vertex. If there is no path to the target null is returned
    * 
    * @param target - the id of the
    */
    public Vertex[] getShortestPathTo(int target) {
        int targetVertexId = -1;
        // Find the target vertex in the vertexData array
        for (int i = 0; i < vertexData.length; i++) {
            // Set the vertex to the target vertex
            if(vertexData[i].vertex.getId() == target) {
                targetVertexId = i;
            }
        }

        // Returns null if the target vertex id is not set.
        if(targetVertexId == -1) return null;

        VertexData currentVertex = vertexData[targetVertexId];
        // Returns null if there is no current vertex.
        if(currentVertex.order == -1) {
            return null;
        }
        Vertex[] shortestPath = new Vertex[currentVertex.order + 1];
        // This method is used to set shortest path
        for(int i = 0; i < shortestPath.length; ++i) {
            shortestPath[currentVertex.order] = currentVertex.vertex;
            currentVertex = currentVertex.prev;
        }
        return shortestPath;
    }

    /**
    * Reset vertex data to initial state. This is called when analyzing a graph to re - start traversal
    */
    public void reset() {
        for (VertexData vertexDatum : vertexData) {
            vertexDatum.minDistance = MAX_DISTANCE;
            vertexDatum.prev = null;
            vertexDatum.visited = false;
            vertexDatum.order = -1;
        }
    }

    /**
    * Visits the nearest vertex. This is done by looking at the minDistance of each vertex to see if there is a vertex with visited == true.
    * 
    * 
    * @return index of the vertex that was visited or - 1 if none was found ( in which case we don't visit
    */
    private int visitNearestVertex() {
        int distance = MAX_DISTANCE;
        int nearestVertexIdx = -1;
        // Find the nearest vertex in the vertexData array.
        for (int i = 0; i < vertexData.length; i++) {
            // If the vertex has been visited
            if(vertexData[i].visited) continue;

            // Set the distance of the vertex to the nearest vertex.
            if (vertexData[i].minDistance < distance) {
                distance = vertexData[i].minDistance;
                nearestVertexIdx = i;
            }
        }
        // Mark the nearest vertex as visited.
        if (nearestVertexIdx != -1) {
            vertexData[nearestVertexIdx].visited = true;
        }
        return nearestVertexIdx;
    }

    private static class VertexData {
        private final Vertex vertex;
        private int minDistance;
        private int order;
        private VertexData prev;
        private boolean visited;

        public VertexData(Vertex vertex) {
            this.vertex = vertex;
            this.minDistance = MAX_DISTANCE;
            this.prev = null;
            this.visited = false;
            this.order = -1;
        }
    }
}
