package com.webler.goliath.graphics;

public class Fog {
    public double fogNear;
    public double fogFar;
    public Color fogColor;

    public Fog(double fogNear, double fogFar, Color fogColor) {
        this.fogNear = fogNear;
        this.fogFar = fogFar;
        this.fogColor = fogColor;
    }
}
