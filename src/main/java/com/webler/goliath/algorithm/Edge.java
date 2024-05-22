package com.webler.goliath.algorithm;

import lombok.Getter;

@Getter
public class Edge {
    private final int source;
    private final int target;
    private final int weight;

    public Edge(int source, int target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

}
