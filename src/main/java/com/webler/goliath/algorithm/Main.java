package com.webler.goliath.algorithm;

public class Main {
    public static void main(String[] args) {
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.buildGraph(new Vertex[]{new Vertex(1), new Vertex(2), new Vertex(3)}, new Edge[] {new Edge(1, 2, 1), new Edge(1, 3, 3), new Edge(2, 3, 5)}, false);
        dijkstra.computePath(1);
        Vertex[] shortestPath = dijkstra.getShortestPathTo(3);
    }
}
