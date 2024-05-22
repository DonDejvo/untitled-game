package com.webler.goliath.math;

public class MathUtils {

    /**
    * Maps a value from one range to another. This is useful for converting between floating point values that are stored in a map such as a map array or map object into a number.
    * 
    * @param value - the value to be mapped. It must be between srcMin and srcMax
    * @param srcMin - the minimum value of the source range.
    * @param srcMax - the maximum value of the source range.
    * @param destMin - the minimum value of the destination range.
    * @param destMax - the maximum value of the destination range.
    * 
    * @return the value mapped to the destination range or NaN if the value is out of range or if there is no mapping
    */
    public static double map(double value, double srcMin, double srcMax, double destMin, double destMax) {
        return destMin + (destMax - destMin) * ((value - srcMin) / (srcMax - srcMin));
    }

    /**
    * Clamps a double value to a range. This is equivalent to Math#min ( double double ) but more efficient for values that are in the range [ min max ]
    * 
    * @param value - the value to be clamped
    * @param min - the minimum value that can be clamped to
    * @param max - the maximum value that can be clamped to
    * 
    * @return the clamped value as a double with respect to the min and max values as defined by the Java™ Language Specification
    */
    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    /**
    * Linearly interpolates between two values. This is useful for interpolating between a and b when you don't know the value of a or b.
    * 
    * @param a - The first value to interpolate. Must be greater than or equal to 0.
    * @param b - The second value to interpolate. Must be greater than or equal to 0.
    * @param t - The interpolation value between 0 and 1. The range of values is [ 0 1 ].
    * 
    * @return The interpolated value. If t is out of range the result is NaN. If t is in the range [ 0 1 ] the result is a + t * ( b - a )
    */
    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
}
