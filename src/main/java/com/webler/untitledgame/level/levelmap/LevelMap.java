package com.webler.untitledgame.level.levelmap;

import com.webler.goliath.graphics.Color;
import com.webler.untitledgame.level.enums.Environment;
import com.webler.untitledgame.level.exceptions.LevelMapFormatException;
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
import java.util.logging.Logger;

@Getter
public class LevelMap implements Serializable {
    protected Logger logger = Logger.getLogger(LevelMap.class.getName());
    public static final String[] ENTITIES = new String[]{
            "player",
            "knight",
            "key",
            "gold",
            "caffe_latte",
            "caffe_mocha",
            "cappuccino",
            "vending_machine",
            "ghost",
            "goblin",
            "ak47",
            "shotgun"
    };
    public static final String TAG = "levelmap";
    @Setter
    private int minX, minY, maxX, maxY;
    @Setter
    private int ceiling;
    @Setter
    private Environment environment;
    @Setter
    private double fogNear, fogFar;
    @Setter
    private Color fogColor;
    @Setter
    private Color ambeintColor;
    @Setter
    private double ambientIntensity;
    @Setter
    private double cameraX, cameraY;
    @Setter
    private double soundVolume;

    private final List<Platform> platforms;
    private final List<Light> lights;
    private final List<Entity> entities;
    private final List<Door> doors;

    public LevelMap() {
        minX = 0;
        minY = 0;
        maxX = 0;
        maxY = 0;
        ceiling = 1;
        fogNear = 25;
        fogFar = 50;
        fogColor = Color.BLACK;
        ambeintColor = Color.WHITE;
        ambientIntensity = 0.05;
        environment = Environment.DUNGEON;
        cameraX = 0;
        cameraY = 0;
        soundVolume = 0.5;
        platforms = new ArrayList<>();
        lights = new ArrayList<>();
        entities = new ArrayList<>();
        doors = new ArrayList<>();
    }

    /**
    * Clears all data stored in this Scene. This is useful when you want to re - use a Scene
    */
    public void clear() {
        platforms.clear();
        lights.clear();
        entities.clear();
        doors.clear();
    }

    /**
    * Adds a platform to the screen. This is called by the platform when it is added to the screen
    * 
    * @param platform - The platform to be
    */
    public void addPlatform(Platform platform) {
        platforms.add(platform);
        // Find the minimum X coordinate of the platform.
        // Find the minimum X coordinate of the platform.
        if(platform.getX() < minX) minX = platform.getX();
        // Find the Y coordinate of the top left corner of the screen
        // Find the Y coordinate of the top left corner of the screen.
        if(platform.getY() < minY) minY = platform.getY();
        // Find the maximum X coordinate of the screen
        // Find the maximum X coordinate of the screen
        if(platform.getX() + platform.getWidth() - 1 > maxX) maxX = platform.getX() + platform.getWidth() - 1;
        // Find the maximum y coordinate of the screen
        // Find the maximum y coordinate of the screen
        if(platform.getY() + platform.getHeight() - 1 > maxY) maxY = platform.getY() + platform.getHeight() - 1;
    }

    /**
    * Adds a light to the scene. This is useful for adding lightes to the scene that are part of the lighting system.
    * 
    * @param light - The light to add to the scene. It must be a Light
    */
    public void addLight(Light light) {
        lights.add(light);
    }

    /**
    * Adds an entity to the list of entities. This method is called by the EntityManager when a new entity is added
    * 
    * @param entity - The entity to be
    */
    public void addEntity(Entity entity) {
        boolean isValid = false;
        for (String s : ENTITIES) {
            // Check if entity is valid.
            // Check if entity is valid.
            if (entity.getName().equals(s)) {
                isValid = true;
                break;
            }
        }
        // Checks if the entity name is valid.
        // Checks if the entity name is valid.
        if(!isValid) {
            logger.warning("Invalid entity name: " + entity.getName());
            return;
        }
        entities.add(entity);
    }

    /**
    * Adds a door to the end of the doors list. This does not check if the door is valid or not.
    * 
    * @param door - The door to add to the doors
    */
    public void addDoor(Door door) {
        doors.add(door);
    }

    /**
    * Loads LevelMap from XML file. This method is used to load LevelMap from XML file. If you want to load LevelMap from XML file use #load ( String )
    * 
    * @param fileName - Name of XML file
    */
    public void load(String fileName) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
        doc.getDocumentElement().normalize();

        Node levelNode = doc.getElementsByTagName(TAG).item(0);
        try {
            deserialize((Element) levelNode);
        } catch (Exception e) {
            throw new LevelMapFormatException(fileName);
        }
    }

    /**
    * Saves the level map to a file. This is a convenience method that uses JAXP's DOM to serialize the level map and then writes it to the file.
    * 
    * @param fileName - the name of the file to write the level map
    */
    public void save(String fileName) throws Exception {
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

    /**
    * Serialize the attributes to the element. This is used to create XML files that are used for debugging purposes
    * 
    * @param element - The element to serialize
    */
    @Override
    public void serialize(Element element) {
//        element.setAttribute("min-x", Integer.toString(minX));
//        element.setAttribute("min-y", Integer.toString(minY));
//        element.setAttribute("max-x", Integer.toString(maxX));
//        element.setAttribute("max-y", Integer.toString(maxY));
        element.setAttribute("ceiling", Integer.toString(ceiling));
        element.setAttribute("environment", String.valueOf(environment));
        element.setAttribute("fog-near", Double.toString(fogNear));
        element.setAttribute("fog-far", Double.toString(fogFar));
        element.setAttribute("fog-color", fogColor.toString());
        element.setAttribute("ambient-color", ambeintColor.toString());
        element.setAttribute("ambient-intensity", Double.toString(ambientIntensity));
        element.setAttribute("camera-x", Double.toString(cameraX));
        element.setAttribute("camera-y", Double.toString(cameraY));
        element.setAttribute("sound-volume", Double.toString(soundVolume));

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

    /**
    * Deserializes the level map. This is called after deserialization and can be used to re - initialize the level map from XML
    * 
    * @param levelMapElement - XML element to be
    */
    @Override
    public void deserialize(Element levelMapElement) {
//        int minX = Integer.parseInt(levelMapElement.getAttribute("min-x"));
//        int minY = Integer.parseInt(levelMapElement.getAttribute("min-y"));
//        int maxX = Integer.parseInt(levelMapElement.getAttribute("max-x"));
//        int maxY = Integer.parseInt(levelMapElement.getAttribute("max-y"));
        int ceiling = Integer.parseInt(levelMapElement.getAttribute("ceiling"));
        Environment environment = levelMapElement.hasAttribute("environment") ? Environment.valueOf(levelMapElement.getAttribute("environment")) : Environment.DUNGEON;
        double fogNear = levelMapElement.hasAttribute("fog-near") ? Double.parseDouble(levelMapElement.getAttribute("fog-near")) : 25;
        double fogFar = levelMapElement.hasAttribute("fog-far") ? Double.parseDouble(levelMapElement.getAttribute("fog-far")) : 50;
        Color fogColor = levelMapElement.hasAttribute("fog-color") ? Color.fromString(levelMapElement.getAttribute("fog-color")) : Color.BLACK;
        Color color = levelMapElement.hasAttribute("ambient-color") ? Color.fromString(levelMapElement.getAttribute("ambient-color")) : Color.WHITE;
        double intensity =levelMapElement.hasAttribute("ambient-intensity") ? Double.parseDouble(levelMapElement.getAttribute("ambient-intensity")) : 0.05;
        double cameraX = levelMapElement.hasAttribute("camera-x") ? Double.parseDouble(levelMapElement.getAttribute("camera-x")) : 0;
        double cameraY = levelMapElement.hasAttribute("camera-y") ? Double.parseDouble(levelMapElement.getAttribute("camera-y")) : 0;
        double soundVolume = levelMapElement.hasAttribute("sound-volume") ? Double.parseDouble(levelMapElement.getAttribute("sound-volume")) : 0.5;

//        this.minX = minX;
//        this.minY = minY;
//        this.maxX = maxX;
//        this.maxY = maxY;
        this.ceiling = ceiling;
        this.environment = environment;
        this.fogNear = fogNear;
        this.fogFar = fogFar;
        this.fogColor = fogColor;
        this.ambeintColor = color;
        this.ambientIntensity = intensity;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.soundVolume = soundVolume;

        NodeList platformNodeList = levelMapElement.getElementsByTagName(Platform.TAG);
        // Creates a new Platform object from the list of platforms.
        // Creates a new Platform object from the list of platforms.
        for(int i = 0; i < platformNodeList.getLength(); ++i) {
            Node platformNode = platformNodeList.item(i);
            Platform platform = new Platform();
            platform.deserialize((Element) platformNode);
            addPlatform(platform);
        }

        NodeList lightNodeList = levelMapElement.getElementsByTagName(Light.TAG);
        // Creates light objects from the light list.
        // Creates light objects from the light list.
        for(int i = 0; i < lightNodeList.getLength(); ++i) {
            Node lightNode = lightNodeList.item(i);
            Light light = new Light();
            light.deserialize((Element) lightNode);
            addLight(light);
        }

        NodeList entityNodeList = levelMapElement.getElementsByTagName(Entity.TAG);
        // Creates all entities in the list of entities.
        // Creates all entities in the list of entities.
        for(int i = 0; i < entityNodeList.getLength(); ++i) {
            Node entityNode = entityNodeList.item(i);
            Entity entity = new Entity();
            entity.deserialize((Element) entityNode);
            addEntity(entity);
        }

        NodeList doorNodeList = levelMapElement.getElementsByTagName(Door.TAG);
        // Deserializes the door elements in the doorNodeList.
        // Deserializes the door elements in the doorNodeList.
        for(int i = 0; i < doorNodeList.getLength(); ++i) {
            Node doorNode = doorNodeList.item(i);
            Door door = new Door();
            door.deserialize((Element) doorNode);
            addDoor(door);
        }
    }

    /**
    * Returns the width of the box. The width is the difference between the left and right sides of the box minus one.
    * 
    * 
    * @return the width of the box as an integer in the range [ 0 width ) or - 1 if the box is
    */
    public int getWidth() {
        return maxX - minX + 1;
    }

    /**
    * Returns the height of the axis. This is the difference between the maximum and minimum values in the axis.
    * 
    * 
    * @return the height of the axis in the axis units ( pixels ). Note that the height will be negative
    */
    public int getHeight() {
        return maxY - minY + 1;
    }
}