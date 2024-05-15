package com.webler.goliath.algorithm;

import lombok.Getter;

@Getter
public class Edge {
    private int source, target;
    private int weight;

    public Edge(int source, int target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

}
