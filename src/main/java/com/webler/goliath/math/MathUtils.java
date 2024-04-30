package com.webler.goliath.math;

public class MathUtils {

    public static double map(double value, double srcMin, double srcMax, double destMin, double destMax) {
        return destMin + (destMax - destMin) * ((value - srcMin) / (srcMax - srcMin));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
}
