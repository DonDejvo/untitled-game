package com.webler.untitledgame.level.levelmap;

import com.webler.goliath.graphics.Color;
import org.w3c.dom.Element;

public class Light implements Serializable {
    public static final String TAG = "light";
    public double x, y, top;
    public double radiusMin, radiusMax;
    public Color color;

    public Light(double x, double y, double top, double radiusMin, double radiusMax, Color color) {
        this.x = x;
        this.y = y;
        this.top = top;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.color = color;
    }

    public Light() {
        x = 0;
        y = 0;
        top = 0;
        radiusMin = 5;
        radiusMax = 10;
        color = Color.WHITE;
    }

    @Override
    public void deserialize(Element element) {
        double top = Double.parseDouble(element.getAttribute("top"));
        double x = Double.parseDouble(element.getAttribute("x"));
        double y = Double.parseDouble(element.getAttribute("y"));
        double radiusMin = Double.parseDouble(element.getAttribute("radius-min"));
        double radiusMax = Double.parseDouble(element.getAttribute("radius-max"));
        Color color = Color.fromString(element.getAttribute("color"));

        this.x = x;
        this.y = y;
        this.top = top;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.color = color;
    }

    @Override
    public void serialize(Element element) {
        element.setAttribute("top", Double.toString(top));
        element.setAttribute("x", Double.toString(x));
        element.setAttribute("y", Double.toString(y));
        element.setAttribute("radius-min", Double.toString(radiusMin));
        element.setAttribute("radius-max", Double.toString(radiusMax));
        element.setAttribute("color", color.toString());
    }
}
