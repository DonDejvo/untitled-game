package com.webler.untitledgame.level.levelmap;

import org.w3c.dom.Element;

public interface Serializable {
    /**
    * Deserializes the element and all its subelements. This method is called by Jackson to deserialize the XML structure that was passed to #serialize ( org. w3c. dom. Element ).
    * 
    * @param element - the XML element to deserialize into this object's
    */
    void deserialize(Element element);
    /**
    * Serializes the element. This is called by the serialize method of the XMLWriter to ensure that the element is serialized before it is passed to the serializer.
    * 
    * @param element - the element to serialize not null but may be
    */
    void serialize(Element element);
}
