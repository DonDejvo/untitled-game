package com.webler.goliath.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DijkstraTest {
    private final Dijkstra dijkstra;

    public DijkstraTest() {
        dijkstra = new Dijkstra();

        Vertex[] vertices = new Vertex[] {
                new Vertex(0),
                new Vertex(1),
                new Vertex(2),
                new Vertex(3),
                new Vertex(4),
                new Vertex(5),
                new Vertex(6),
                new Vertex(7),
                new Vertex(8),
        };

        Edge[] edges = new Edge[] {
                new Edge(0, 1, 4),
                new Edge(0, 7, 8),
                new Edge(1, 2, 8),
                new Edge(1, 7, 11),
                new Edge(2, 3, 7),
                new Edge(2, 8, 2),
                new Edge(3, 4, 9),
                new Edge(3, 5, 14),
                new Edge(4, 5, 10),
                new Edge(5, 6, 2),
                new Edge(6, 7, 1),
                new Edge(6, 8, 6),
                new Edge(7, 8, 7),
        };

        dijkstra.buildGraph(vertices, edges, false);
    }

    @BeforeEach
    public void setup() {
        dijkstra.reset();
        dijkstra.computePath(0);
    }

    @Test
    public void testGetVerticesMethod() {
        assertEquals(9, dijkstra.getVertices().length);
    }

    @Test
    public void testGetEdgesMethod() {
        assertEquals(13, dijkstra.getEdges().length);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 4",
            "2, 12",
            "3, 19",
            "4, 21",
            "5, 11",
            "6, 9",
            "7, 8",
            "8, 14"
    })
    public void testComputePathMethod(int target, int expectedDistance) {
        assertEquals(expectedDistance, dijkstra.getDistance(target));
    }

    @ParameterizedTest
    @MethodSource
    public void testGetShortestPathToMethod(int target, int[] expectedPathIds) {
        Vertex[] path = dijkstra.getShortestPathTo(target);
        assertEquals(expectedPathIds.length, path.length);
        for (int i = 0; i < path.length; ++i) {
            assertEquals(expectedPathIds[i], path[i].getId());
        }
    }

    public static Stream<Arguments> testGetShortestPathToMethod() {
        return Stream.of(Arguments.of(1, new int[] { 0, 1 }),
                Arguments.of(2, new int[] { 0, 1, 2 }),
                Arguments.of(3, new int[] { 0, 1, 2, 3 }),
                Arguments.of(4, new int[] { 0, 7, 6, 5, 4 }),
                Arguments.of(5, new int[] { 0, 7, 6, 5 }),
                Arguments.of(6, new int[] { 0, 7, 6 }),
                Arguments.of(7, new int[] { 0, 7 }),
                Arguments.of(8, new int[] { 0, 1, 2, 8 }));
    }
}
