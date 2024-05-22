package com.webler.goliath.graphics;

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

    /**
    * Returns an array containing all components of this Color. The array is backed by the Color object so changes to this object are reflected in the array and vice - versa
    */
    public float[] toArray() {
        return new float[] { (float)r, (float)g, (float)b, (float)a };
    }

    /**
    * Creates a color from an array of float values. The array must be in the format returned by #toFloatArray ( float [] ).
    * 
    * @param array - the array to be converted to a color.
    * 
    * @return the color created from the array. Never null but may be null if the array is null or contains only values that are valid for the type of the color
    */
    public static Color fromArray(float[] array) {
        return new Color(array[0], array[1], array[2]);
    }

    /**
    * Returns a String representation of this Color. The format is " #RRGGBBAA ".
    * 
    * 
    * @return a String representation of this Color in the format " #RRGGBBAA ". Note that this representation is not normalized
    */
    @Override
    public String toString() {
        return String.format(Locale.US, "%.3f %.3f %.3f %.3f", r, g, b, a);
    }

    /**
    * Creates a color from a string. The string should be in the format r g b a. If you don't know what you are doing use Color#toString ()
    * 
    * @param color - the string to parse.
    * 
    * @return the color parsed from the string. Never null but may be null in cases where it is undesirable
    */
    public static Color fromString(String color) {
        String[] parts = color.split(" ");
        double r = Double.parseDouble(parts[0]);
        double g = Double.parseDouble(parts[1]);
        double b = Double.parseDouble(parts[2]);
        double a = Double.parseDouble(parts[3]);
        return new Color(r, g, b, a);
    }
}
