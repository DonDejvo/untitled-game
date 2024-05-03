package com.webler.goliath.graphics;

import org.joml.Vector4f;

import java.util.Locale;

public class Color {
    public static final Color WHITE = new Color(1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(1, 0, 0);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color YELLOW = new Color(1, 1, 0);
    public static final Color ORANGE = new Color(1, 0.5, 0);
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
    public double r;
    public double g;
    public double b;
    public double a;

    public Color(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(double r, double g, double b) {
        this(r, g, b, 1);
    }

    public Color(Color color) {
        this(color.r, color.g, color.b, color.a);
    }

    public float[] toArray() {
        return new float[] { (float)r, (float)g, (float)b, (float)a };
    }

    public static Color fromArray(float[] array) {
        return new Color(array[0], array[1], array[2]);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.3f %.3f %.3f %.3f", r, g, b, a);
    }

    public static Color fromString(String color) {
        String[] parts = color.split(" ");
        double r = Double.parseDouble(parts[0]);
        double g = Double.parseDouble(parts[1]);
        double b = Double.parseDouble(parts[2]);
        double a = Double.parseDouble(parts[3]);
        return new Color(r, g, b, a);
    }
}
