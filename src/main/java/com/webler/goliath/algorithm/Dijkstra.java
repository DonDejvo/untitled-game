package com.webler.goliath.algorithm;

public class Dijkstra {
    private static final int MAX_DISTANCE = 1000000;
    private VertexData[] vertexData;
    private int[][] edgeGrid;

    public void buildGraph(Vertex[] vertices, Edge[] edges, boolean directed) {
        edgeGrid = new int[vertices.length][vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            for (int j = 0; j < vertices.length; j++) {
                edgeGrid[i][j] = i == j ? 0 : MAX_DISTANCE;
            }
        }
        vertexData = new VertexData[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vertexData[i] = new VertexData(vertices[i]);
        }
        for (Edge edge : edges) {
            int source = edge.getSource();
            int target = edge.getTarget();
            int sourceIdx = -1, targetIdx = -1;
            for (int i = 0; i < vertexData.length; i++) {
                if(vertexData[i].vertex.getId() == source) {
                    sourceIdx = i;
                }
                if(vertexData[i].vertex.getId() == target) {
                    targetIdx = i;
                }
            }
            if(sourceIdx != -1 && targetIdx != -1) {
                edgeGrid[sourceIdx][targetIdx] = edge.getWeight();
                if (!directed) {
                    edgeGrid[targetIdx][sourceIdx] = edge.getWeight();
                }
            }
        }
    }

    public void computePath(int source) {
        int visitedCount = 0;
        int sourceVertexIdx = -1;
        for (int i = 0; i < vertexData.length; i++) {
            if(vertexData[i].vertex.getId() == source) {
                sourceVertexIdx = i;
            }
        }

        if(sourceVertexIdx == -1) return;

        vertexData[sourceVertexIdx].minDistance = 0;
        vertexData[sourceVertexIdx].order = 0;

        while (visitedCount < vertexData.length) {
            int currentIdx = visitNearestVertex();

            if(currentIdx == -1) break;

            VertexData currentVertex = vertexData[currentIdx];
            for(int i = 0; i < edgeGrid[currentIdx].length; ++i) {
                VertexData neighbor = vertexData[i];
                int distance = currentVertex.minDistance + edgeGrid[currentIdx][i];
                if(distance < neighbor.minDistance) {
                    neighbor.minDistance = distance;
                    neighbor.order = currentVertex.order + 1;
                    neighbor.prev = currentVertex;
                }
            }
            ++visitedCount;
        }
    }

    public Vertex[] getShortestPathTo(int target) {
        int targetVertexId = -1;
        for (int i = 0; i < vertexData.length; i++) {
            if(vertexData[i].vertex.getId() == target) {
                targetVertexId = i;
            }
        }

        if(targetVertexId == -1) return null;

        VertexData currentVertex = vertexData[targetVertexId];
        if(currentVertex.order == -1) {
            return null;
        }
        Vertex[] shortestPath = new Vertex[currentVertex.order + 1];
        for(int i = 0; i < shortestPath.length; ++i) {
            shortestPath[currentVertex.order] = currentVertex.vertex;
            currentVertex = currentVertex.prev;
        }
        return shortestPath;
    }

    public void reset() {
        for (int i = 0; i < vertexData.length; i++) {
            vertexData[i].minDistance = MAX_DISTANCE;
            vertexData[i].prev = null;
            vertexData[i].visited = false;
            vertexData[i].order = -1;
        }
    }

    private int visitNearestVertex() {
        int distance = MAX_DISTANCE;
        int nearestVertexIdx = -1;
        for (int i = 0; i < vertexData.length; i++) {
            if(vertexData[i].visited) continue;

            if (vertexData[i].minDistance < distance) {
                distance = vertexData[i].minDistance;
                nearestVertexIdx = i;
            }
        }
        if (nearestVertexIdx != -1) {
            vertexData[nearestVertexIdx].visited = true;
        }
        return nearestVertexIdx;
    }

    private static class VertexData {
        private Vertex vertex;
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
