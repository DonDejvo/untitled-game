package com.webler.untitledgame.level.levelmap;

import com.webler.goliath.graphics.Color;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

@Setter
@Getter
public class Light implements Serializable {
    public static final String TAG = "light";
    private double x, y, top;
    private double radiusMin, radiusMax;
    private Color color;
    private double intensity;

    public Light(double x, double y, double top, double radiusMin, double radiusMax, Color color, double intensity) {
        this.x = x;
        this.y = y;
        this.top = top;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.color = color;
        this.intensity = intensity;
    }

    public Light() {
    }

    /**
    * Deserializes the object from XML. This is used to deserialize the attributes that are part of the Shape2D
    * 
    * @param element - XML element that contains the
    */
    @Override
    public void deserialize(Element element) {
        double top = Double.parseDouble(element.getAttribute("top"));
        double x = Double.parseDouble(element.getAttribute("x"));
        double y = Double.parseDouble(element.getAttribute("y"));
        double radiusMin = Double.parseDouble(element.getAttribute("radius-min"));
        double radiusMax = Double.parseDouble(element.getAttribute("radius-max"));
        Color color = Color.fromString(element.getAttribute("color"));
        double intensity = element.hasAttribute("intensity") ? Double.parseDouble(element.getAttribute("intensity")) : 1;

        this.x = x;
        this.y = y;
        this.top = top;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.color = color;
        this.intensity = intensity;
    }

    /**
    * Serializes this object to XML. This is used to create XML elements that are used for rendering. The attributes of this object are : top x y radius - min radius - max color intensity
    * 
    * @param element - XML element to serialize this object to. Must not be null
    */
    @Override
    public void serialize(Element element) {
        element.setAttribute("top", Double.toString(top));
        element.setAttribute("x", Double.toString(x));
        element.setAttribute("y", Double.toString(y));
        element.setAttribute("radius-min", Double.toString(radiusMin));
        element.setAttribute("radius-max", Double.toString(radiusMax));
        element.setAttribute("color", color.toString());
        element.setAttribute("intensity", Double.toString(intensity));
    }
}
