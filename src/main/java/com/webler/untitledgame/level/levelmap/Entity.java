package com.webler.untitledgame.level.levelmap;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Element;

@Setter
@Getter
public class Entity implements Serializable{
    public static final String TAG = "entity";
    private String name;
    private double x, y;

    public Entity(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Entity() {
    }

    /**
    * Deserializes the object from XML. This is used to deserialize the data that was saved with #save ()
    * 
    * @param element - XML element to deserialize
    */
    @Override
    public void deserialize(Element element) {
        String name = element.getAttribute("name");
        double x = Double.parseDouble(element.getAttribute("x"));
        double y = Double.parseDouble(element.getAttribute("y"));

        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
    * Serializes this object to XML. This is used to create XML elements that are used for debugging purposes such as printing the results of an object's query.
    * 
    * @param element - XML element to serialize to. Must not be null
    */
    @Override
    public void serialize(Element element) {
        element.setAttribute("name", name);
        element.setAttribute("x", Double.toString(x));
        element.setAttribute("y", Double.toString(y));
    }
}
