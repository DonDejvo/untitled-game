package com.webler.untitledgame.level.levelmap;

import com.webler.goliath.graphics.Color;
import org.w3c.dom.Element;

public class Entity implements Serializable{
    public static final String TAG = "entity";
    public String name;
    public double x, y;

    public Entity(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Entity() {
    }

    @Override
    public void deserialize(Element element) {
        String name = element.getAttribute("name");
        double x = Double.parseDouble(element.getAttribute("x"));
        double y = Double.parseDouble(element.getAttribute("y"));

        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public void serialize(Element element) {
        element.setAttribute("name", name);
        element.setAttribute("x", Double.toString(x));
        element.setAttribute("y", Double.toString(y));
    }
}
