package com.webler.untitledgame.components;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.dialogs.DialogNode;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.Bilboard;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.controllers.*;
import com.webler.untitledgame.level.inventory.Inventory;
import com.webler.untitledgame.level.levelmap.*;
import org.joml.Vector3d;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.*;

public class Level extends Component {
    public static final int TILE_SIZE = 4;
    private final LevelMap levelMap;
    private GridItem[][] grid;
    private String path;
    private GameObject player;
    private Map<String, List<GameObject>> objectGroups;

    public Level() {
        this.levelMap = new LevelMap();
        path = null;
        player = null;
        objectGroups = new HashMap<>();
        buildGrid();
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }

    public LevelMap getLevelMap() {
        return levelMap;
    }

    public void load(String fileName) {
        try {
            levelMap.clear();
            levelMap.load(fileName);
            path = fileName;
            buildGrid();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void save(String fileName) {
        try {
            levelMap.save(fileName);
            path = fileName;
        } catch (TransformerException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public int getBlockTop(int x, int y) {
        return grid[y][x].top;
    }

    public boolean isBlockAt(int x, int y, int z) {
        if(x < 0 || z < 0 || z >= grid.length || x >= grid[0].length) {
            return true;
        }
        GridItem gridItem = grid[z][x];
        return gridItem.top == -1 || y < gridItem.top || y >= gridItem.ceiling;
    }

    public boolean isBlockAtBox(Vector3d min, Vector3d max) {
        int blockMinX = (int)Math.floor(min.x / TILE_SIZE);
        int blockMinZ = (int)Math.floor(min.z / TILE_SIZE);
        int blockMaxX = (int)Math.floor(max.x / TILE_SIZE);
        int blockMaxZ = (int)Math.floor(max.z / TILE_SIZE);
        int blockMinY = (int) Math.floor(min.y / TILE_SIZE);
        int blockMaxY = (int) Math.floor(max.y / TILE_SIZE);
        for(int z = blockMinZ; z <= blockMaxZ; ++z) {
            for(int x = blockMinX; x <= blockMaxX; ++x) {
                for(int y = blockMinY; y <= blockMaxY; ++y) {
                    if(isBlockAt(x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void addObjectToGroup(GameObject object, String group) {
        if(!objectGroups.containsKey(group)) {
            objectGroups.put(group, new ArrayList<>());
        }
        objectGroups.get(group).add(object);
    }

    public void removeObjectFromGroup(GameObject object, String group) {
        if(objectGroups.containsKey(group)) {
            objectGroups.get(group).remove(object);
        }
    }

    public List<GameObject> getObjectsByGroup(String group) {
        return objectGroups.get(group);
    }

    public void buildLevel() {
        createDoors();
        createEntities();
        createLights();
    }

    public GameObject getPlayer() {
        return player;
    }

    private void buildGrid() {
        int mapWidth = levelMap.getWidth();
        int mapHeight = levelMap.getHeight();
        grid = new GridItem[mapHeight][mapWidth];
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                grid[i][j] = new GridItem(-1, levelMap.ceiling);
            }
        }
        for(Platform platform : levelMap.getPlatforms()) {
            int x1, x2, y1, y2;
            x1 = platform.x - levelMap.minX;
            x2 = x1 + platform.width - 1;
            y1 = platform.y - levelMap.minY;
            y2 = y1 + platform.height- 1;

            for(int y = y1; y <= y2; ++y) {
                for(int x = x1; x <= x2; ++x) {
                    GridItem gridItem = grid[y][x];
                    if(gridItem.top <= platform.top) {
                        gridItem.top = platform.top;
                    }
                    if(gridItem.ceiling >= platform.ceiling) {
                        gridItem.ceiling = platform.ceiling;
                    }
                }
            }
        }
    }

    private static class GridItem {
        private int top, ceiling;

        public GridItem(int top, int ceiling) {
            this.top = top;
            this.ceiling = ceiling;
        }
    }

    private void createEntities() {
        Scene scene = getEntity().getScene();
        List<Entity> entities = levelMap.getEntities();
        for (Entity entity : entities) {
            GameObject go = new GameObject(scene);
            double y = getBlockTop((int)Math.floor(entity.x - levelMap.minX), (int)Math.floor(entity.y - levelMap.minY));
            Vector3d position = new Vector3d(entity.x - levelMap.minX, y, entity.y - levelMap.minY).mul(Level.TILE_SIZE);
            go.transform.position.set(position);
            switch (entity.name) {
                case "player": {
                    player = go;
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(1.5, 3, 1.5));
                    go.addComponent("Collider", collider);
                    Inventory inventory = new Inventory();
                    PlayerController playerController = new PlayerController(this, scene.getCamera(), collider, inventory);
                    go.addComponent("Controller", playerController);
                    go.addComponent("Inventory", inventory);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "catgirl": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(1.5, 3, 1.5));
                    go.addComponent("Collider", collider);
                    Sprite sprite = new Sprite(AssetPool.getTexture("assets/images/4-3.png"));
                    sprite.setWidth(2);
                    sprite.setHeight(3);
                    SpriteRenderer renderer = new SpriteRenderer(sprite, -1);
                    go.addComponent("Renderer", renderer);
                    go.addComponent("Bilboard", new Bilboard());
                    go.addComponent("Controller", new NpcController(this, collider));
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    dialogComponent.addOption(new DialogOption(true, "Hello.",
                            new DialogNode("maid_chan__first",
                                new DialogNode("you__confusion",
                                    new DialogNode("maid_chan__second", null)))));
                    dialogComponent.addOption(new DialogOption(false, "(No-repeat option)",
                            new DialogNode("maid_chan__no-repeat", null)));
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "vendingmachine": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2, 4, 2));
                    go.addComponent("Collider", collider);
                    Sprite sprite = new Sprite(AssetPool.getTexture("assets/images/Vending_Machine_21.png"));
                    sprite.setWidth(2);
                    sprite.setHeight(4);
                    SpriteRenderer renderer = new SpriteRenderer(sprite, -1);
                    go.addComponent("Renderer", renderer);
                    go.addComponent("Bilboard", new Bilboard());
                    go.addComponent("Controller", new NpcController(this, collider));
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "key": {
                    Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/key.png"));
                    sprite.setWidth(1);
                    sprite.setHeight(1);
                    SpriteRenderer renderer = new SpriteRenderer(sprite, -1);
                    go.addComponent("Renderer", renderer);
                    go.addComponent("Bilboard", new Bilboard());
                    go.addComponent("Controller", new ItemController(this, entity.name));
                    go.transform.position.y += sprite.getHeight();
                    break;
                }
            }

            scene.add(go);
        }
    }

    private void createLights() {
        Scene scene = getEntity().getScene();
        List<Light> lights = levelMap.getLights();
        for (Light light : lights) {
            GameObject go = new GameObject(scene);
            go.transform.position.set(new Vector3d(light.x - levelMap.minX, light.top, light.y - levelMap.minY).mul(Level.TILE_SIZE));
            SpotLight spotLight = new SpotLight(new Color(light.color), light.radiusMin * Level.TILE_SIZE, light.radiusMax * Level.TILE_SIZE);
            go.addComponent("Light", spotLight);
            Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/torch.png"));
            sprite.setWidth(1);
            sprite.setHeight(1);
            go.addComponent("Renderer", new SpriteRenderer(sprite, -1));
            go.addComponent("Bilboard", new Bilboard());
            scene.add(go);
        }

        GameObject ambientLightGameObject = new GameObject(scene);
        ambientLightGameObject.addComponent("AmbientLight", new AmbientLight(new Color(0.05, 0.05, 0.05)));
        scene.add(ambientLightGameObject);

//        GameObject fogGameObject = new GameObject(scene);
//        fogGameObject.addComponent("Fog", new Fog(25, 50, Color.BLACK));
//        scene.add(fogGameObject);
    }

    private void createDoors() {
        Scene scene = getEntity().getScene();
        List<Door> doors = levelMap.getDoors();
        for (Door door : doors) {
            GameObject doorGameObject = new GameObject(scene);
            MeshRenderer renderer = new MeshRenderer(new Cube(AssetPool.getTexture("assets/tiles/door.png").getTexId()));
            renderer.offset.x = 0.5;
            doorGameObject.addComponent("Renderer", renderer);
            double y = getBlockTop((door.x - levelMap.minX), (door.y - levelMap.minY));
            Vector3d position = new Vector3d(door.x - levelMap.minX + 0.5, y + 0.5, door.y - levelMap.minY + 0.5);
            switch (door.direction) {
                case LEFT:
                    position.z += 0.5;
                    break;
                case RIGHT:
                    position.z -= 0.5;
                    break;
                case UP:
                    position.x += 0.5;
                    break;
                case DOWN:
                    position.x -= 0.5;
                    break;
            }
            doorGameObject.transform.position.set(position.mul(Level.TILE_SIZE));
            doorGameObject.transform.scale.set(Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE * 0.125);
            BoxCollider3D collider = new BoxCollider3D(new Vector3d(4, 4, 4));
            collider.offset.x = 0.5;
            doorGameObject.addComponent("Collider", collider);
            doorGameObject.addComponent("Controller", new DoorController(this, collider, door.direction));
            scene.add(doorGameObject);
        }
    }
}
