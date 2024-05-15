package com.webler.untitledgame.level.levelmap;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LevelMap implements Serializable {
    public static final String[] ENTITIES = new String[]{
            "player",
            "cat_girl_1",
            "cat_girl_2",
            "cat_girl_3",
            "key",
            "gold",
            "caffe_latte",
            "caffe_mocha",
            "cappuccino",
            "vending_machine",
            "ghost",
            "ak47",
            "shotgun"
    };
    public static final String TAG = "levelmap";
    @Setter
    @Getter
    private int minX, minY, maxX, maxY;
    @Setter
    @Getter
    private int ceiling;
    @Setter
    @Getter
    private Environment environment;

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
        environment = Environment.DUNGEON;
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
        if(platform.getX() < minX) minX = platform.getX();
        if(platform.getY() < minY) minY = platform.getY();
        if(platform.getX() + platform.getWidth() - 1 > maxX) maxX = platform.getX() + platform.getWidth() - 1;
        if(platform.getY() + platform.getHeight() - 1 > maxY) maxY = platform.getY() + platform.getHeight() - 1;
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void addEntity(Entity entity) throws LevelMapFormatException {
        boolean isValid = false;
        for (String s : ENTITIES) {
            if (entity.getName().equals(s)) {
                isValid = true;
                break;
            }
        }
        if(!isValid) {
            throw new LevelMapFormatException("Entity name contains invalid value: " + entity.getName());
        }
        entities.add(entity);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public void load(String fileName) throws LevelMapFormatException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            doc.getDocumentElement().normalize();

            Node levelNode = doc.getElementsByTagName(TAG).item(0);
            deserialize((Element) levelNode);
        } catch (Exception e) {
            throw new LevelMapFormatException(e.getMessage());
        }
    }

    public void save(String fileName) throws LevelMapFormatException {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new LevelMapFormatException(e.getMessage());
        }
    }

    @Override
    public void serialize(Element element) {
        element.setAttribute("min-x", Integer.toString(minX));
        element.setAttribute("min-y", Integer.toString(minY));
        element.setAttribute("max-x", Integer.toString(maxX));
        element.setAttribute("max-y", Integer.toString(maxY));
        element.setAttribute("ceiling", Integer.toString(ceiling));
        element.setAttribute("environment", String.valueOf(environment));

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
    public void deserialize(Element levelMapElement) throws LevelMapFormatException {
        int minX = Integer.parseInt(levelMapElement.getAttribute("min-x"));
        int minY = Integer.parseInt(levelMapElement.getAttribute("min-y"));
        int maxX = Integer.parseInt(levelMapElement.getAttribute("max-x"));
        int maxY = Integer.parseInt(levelMapElement.getAttribute("max-y"));
        int ceiling = Integer.parseInt(levelMapElement.getAttribute("ceiling"));
        String environmentAttr = levelMapElement.getAttribute("environment");
        Environment environment = environmentAttr.isEmpty() ? Environment.HOUSE : Environment.valueOf(environmentAttr);

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.ceiling = ceiling;
        this.environment = environment;

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