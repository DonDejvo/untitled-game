package com.webler.untitledgame.level.levelmap;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelMap implements Serializable {
    public static final String TAG = "levelmap";
    public int minX, minY, maxX, maxY;
    public int ceiling;
    private List<Platform> platforms;
    private List<Light> lights;
    private List<Entity> entities;
    private List<Door> doors;

    public LevelMap() {
        minX = 0;
        minY = 0;
        maxX = 0;
        maxY = 0;
        ceiling = 1;
        platforms = new ArrayList<>();
        lights = new ArrayList<>();
        entities = new ArrayList<>();
        doors = new ArrayList<>();
    }

    public void clear() {
        platforms.clear();
        lights.clear();
        entities.clear();
        doors.clear();
    }

    public void addPlatform(Platform platform) {
        platforms.add(platform);
        if(platform.x < minX) minX = platform.x;
        if(platform.y < minY) minY = platform.y;
        if(platform.x + platform.width - 1 > maxX) maxX = platform.x + platform.width - 1;
        if(platform.y + platform.height - 1 > maxY) maxY = platform.y + platform.height - 1;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public void load(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        Node levelNode = doc.getElementsByTagName(TAG).item(0);
        try {
            deserialize((Element) levelNode);
        } catch (NullPointerException e) {
            throw new ParserConfigurationException(e.getMessage());
        }
    }

    public void save(String fileName) throws TransformerException, IOException, ParserConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();

        Element levelMapElement = doc.createElement(TAG);
        serialize(levelMapElement);
        doc.appendChild(levelMapElement);

        DOMSource source = new DOMSource(doc);

        FileWriter writer = new FileWriter(fileName);
        StreamResult result = new StreamResult(writer);

        transformer.transform(source, result);
    }

    @Override
    public void serialize(Element element) {
        element.setAttribute("min-x", Integer.toString(minX));
        element.setAttribute("min-y", Integer.toString(minY));
        element.setAttribute("max-x", Integer.toString(maxX));
        element.setAttribute("max-y", Integer.toString(maxY));
        element.setAttribute("ceiling", Integer.toString(ceiling));

        for (Platform platform : platforms) {
            Element platformElement = element.getOwnerDocument().createElement(Platform.TAG);
            platform.serialize(platformElement);
            element.appendChild(platformElement);
        }

        for (Light light : lights) {
            Element lightElement = element.getOwnerDocument().createElement(Light.TAG);
            light.serialize(lightElement);
            element.appendChild(lightElement);
        }

        for (Entity entity : entities) {
            Element entityElement = element.getOwnerDocument().createElement(Entity.TAG);
            entity.serialize(entityElement);
            element.appendChild(entityElement);
        }

        for (Door door : doors) {
            Element doorElement = element.getOwnerDocument().createElement(Door.TAG);
            door.serialize(doorElement);
            element.appendChild(doorElement);
        }
    }

    @Override
    public void deserialize(Element levelMapElement) {
        int minX = Integer.parseInt(levelMapElement.getAttribute("min-x"));
        int minY = Integer.parseInt(levelMapElement.getAttribute("min-y"));
        int maxX = Integer.parseInt(levelMapElement.getAttribute("max-x"));
        int maxY = Integer.parseInt(levelMapElement.getAttribute("max-y"));
        int ceiling = Integer.parseInt(levelMapElement.getAttribute("ceiling"));

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.ceiling = ceiling;

        NodeList platformNodeList = levelMapElement.getElementsByTagName(Platform.TAG);
        for(int i = 0; i < platformNodeList.getLength(); ++i) {
            Node platformNode = platformNodeList.item(i);
            Platform platform = new Platform();
            platform.deserialize((Element) platformNode);
            addPlatform(platform);
        }

        NodeList lightNodeList = levelMapElement.getElementsByTagName(Light.TAG);
        for(int i = 0; i < lightNodeList.getLength(); ++i) {
            Node lightNode = lightNodeList.item(i);
            Light light = new Light();
            light.deserialize((Element) lightNode);
            addLight(light);
        }

        NodeList entityNodeList = levelMapElement.getElementsByTagName(Entity.TAG);
        for(int i = 0; i < entityNodeList.getLength(); ++i) {
            Node entityNode = entityNodeList.item(i);
            Entity entity = new Entity();
            entity.deserialize((Element) entityNode);
            addEntity(entity);
        }

        NodeList doorNodeList = levelMapElement.getElementsByTagName(Door.TAG);
        for(int i = 0; i < doorNodeList.getLength(); ++i) {
            Node doorNode = doorNodeList.item(i);
            Door door = new Door();
            door.deserialize((Element) doorNode);
            addDoor(door);
        }
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Door> getDoors() {
        return doors;
    }

    public int getWidth() {
        return maxX - minX + 1;
    }

    public int getHeight() {
        return maxY - minY + 1;
    }
}