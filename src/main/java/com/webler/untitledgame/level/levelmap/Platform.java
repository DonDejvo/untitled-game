package com.webler.untitledgame.level.levelmap;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

@Setter
@Getter
public class Platform implements Serializable {
    public static final String TAG = "platform";
    private int x, y, width, height, top, ceiling;

    public Platform(int x, int y, int width, int height, int top, int ceiling) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;
        this.ceiling = ceiling;
    }

    public Platform() {
    }

    /**
    * Deserializes the bounding box from the DOM. This is used to deserialize the bounding box that was serialized using platform. serialize
    * 
    * @param platformElement - the DOM element that contains the
    */
    @Override
    public void deserialize(Element platformElement) {
        int width = Integer.parseInt(platformElement.getAttribute("width"));
        int height = Integer.parseInt(platformElement.getAttribute("height"));
        int top = Integer.parseInt(platformElement.getAttribute("top"));
        int x = Integer.parseInt(platformElement.getAttribute("x"));
        int y = Integer.parseInt(platformElement.getAttribute("y"));
        int ceiling = Integer.parseInt(platformElement.getAttribute("ceiling"));

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;
        this.ceiling = ceiling;
    }

    /**
    * Serializes the attributes of this rectangle to the given element. This is used for serialization of rectangles that are stored in a platform XML file such as a. jpg or. png file
    * 
    * @param platformElement - The element to serialize
    */
    @Override
    public void serialize(Element platformElement) {
        platformElement.setAttribute("width", Integer.toString(width));
        platformElement.setAttribute("height", Integer.toString(height));
        platformElement.setAttribute("top", Integer.toString(top));
        platformElement.setAttribute("x", Integer.toString(x));
        platformElement.setAttribute("y", Integer.toString(y));
        platformElement.setAttribute("ceiling", Integer.toString(ceiling));
    }

    /**
    * Compares this object with another object. This is used to determine if two objects are equal or not.
    * 
    * @param obj - the object to compare to. May be null.
    * 
    * @return true if the objects are equal false otherwise. Note that false is always returned for objects that are not equal
    */
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    /**
    * Returns true if this platform is equal to the other platform. This is the case if both platforms have the same position and size
    * 
    * @param other - the platform to compare to
    * 
    * @return true if the platforms are equal false otherwise ( including false positives in the case of an incorrect position
    */
    public boolean equals(Platform other) {
        return getX() == other.getX() && getY() == other.getY() && getWidth() == other.getWidth() && getHeight() == other.getHeight() && getTop() == other.getTop() && getCeiling() == other.getCeiling();
    }
}