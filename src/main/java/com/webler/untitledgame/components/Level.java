package com.webler.untitledgame.components;

import com.webler.goliath.algorithm.Dijkstra;
import com.webler.goliath.algorithm.Edge;
import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.animation.components.Animator;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;
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
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;
import java.util.stream.Collectors;

public class Level extends Component {
    public static final int TILE_SIZE = 4;
    private final LevelMap levelMap;
    private GridItem[][] grid;
    private String path;
    private GameObject player;
    private final Map<String, List<GameObject>> objectGroups;
    private final List<LevelObject> levelObjectRegistry;
    private final Dijkstra dijkstra;

    public Level() {
        this.levelMap = new LevelMap();
        path = null;
        player = null;
        objectGroups = new HashMap<>();
        levelObjectRegistry = new ArrayList<>();
        dijkstra = new Dijkstra();
        buildGrid();
    }

    public List<LevelObject> getRegisteredObjects() {
        return levelObjectRegistry;
    }

    public LevelObject getRegisteredObject(String identifier) {
        return levelObjectRegistry.stream()
                .filter(item -> item.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }

    public List<LevelObject> getRegisteredObjects(LevelObjectType type) {
        return levelObjectRegistry.stream()
                .filter(item -> item.getType() == type)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void init() {
        Spritesheet tilesetSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png");
        Spritesheet catgirlsSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/catgirls.png");
        Spritesheet houseSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/house_asset.png");
        Spritesheet ghostSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/ghost.png");

        levelObjectRegistry.add(new LevelEntity("player", "Player",
                new Sprite(AssetPool.getTexture("untitled-game/images/player.png")), new Vector2d(0.75, 0.75), 25));
        levelObjectRegistry.add(new LevelEntity("vending_machine", "Vending Machine",
                new Sprite(AssetPool.getTexture("untitled-game/images/vending_machine.png")), new Vector2d(0.5, 1), 20));
        levelObjectRegistry.add(new LevelItem("gold", "Gold",
                tilesetSpritesheet.getSprite(0), new Vector2d(0.5, 0.5), 50, "Needed to purchase items in vending machines.", 1));
        levelObjectRegistry.add(new LevelItem("key", "Key", tilesetSpritesheet.getSprite(5), new Vector2d(0.5, 0.5),50, "Required to unlock door.",
                0));
        levelObjectRegistry.add(new LevelItem("caffe_latte", "Caffe Latte", tilesetSpritesheet.getSprite(6), new Vector2d(0.5, 0.5), 50, "Restores your health.",
                1));
        levelObjectRegistry.add(new LevelItem("caffe_mocha", "Caffe Mocha", tilesetSpritesheet.getSprite(7), new Vector2d(0.5, 0.5), 50, "Boosts your firing power.",
                 2));
        levelObjectRegistry.add(new LevelItem("cappuccino", "Cappuccino", tilesetSpritesheet.getSprite(8), new Vector2d(0.5, 0.5), 50, "Increases your speed.",
                3));
        levelObjectRegistry.add(new LevelEntity("cat_girl_1", "Cat girl 1",
                catgirlsSpritesheet.getSprite(0), new Vector2d(0.5, 0.75), 24));
        levelObjectRegistry.add(new LevelEntity("cat_girl_2", "Cat girl 2",
                catgirlsSpritesheet.getSprite(1), new Vector2d(0.5, 0.75), 24));
        levelObjectRegistry.add(new LevelEntity("cat_girl_3", "Cat girl 3",
                catgirlsSpritesheet.getSprite(2), new Vector2d(0.5, 0.75), 24));
        levelObjectRegistry.add(new LevelEntity("pc_desk", "PC Desk",
                houseSpritesheet.getSprite(6), new Vector2d(0.675, 0.675), 20));
        levelObjectRegistry.add(new LevelEntity("drawer", "Drawer",
                houseSpritesheet.getSprite(4), new Vector2d(0.675, 0.675), 20));
        levelObjectRegistry.add(new LevelEntity("cupboard", "Cupboard",
                houseSpritesheet.getSprite(2), new Vector2d(0.675, 0.675), 20));
        levelObjectRegistry.add(new LevelEntity("sink", "Sink",
                houseSpritesheet.getSprite(3), new Vector2d(0.675, 0.675), 20));
        levelObjectRegistry.add(new LevelEntity("ghost", "Ghost",
                ghostSpritesheet.getSprite(0), new Vector2d(0.75, 0.75), 24));
    }

    @Override
    public void start() {
        init();
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

    public void load(String fileName) throws LevelMapFormatException {
        levelMap.clear();
        levelMap.load(fileName);
        path = fileName;
        buildGrid();
    }

    public void save(String fileName) throws LevelMapFormatException {
        levelMap.save(fileName);
        path = fileName;
    }

    public String getPath() {
        return path;
    }

    public int getBlockTop(int x, int y) {
        if(x < 0 || y < 0 || y >= grid.length || x >= grid[0].length) {
            return 0;
        }
        return grid[y][x].top;
    }

    public Platform getBlockPlatform(int x, int y) {
        if(x < 0 || y < 0 || y >= grid.length || x >= grid[0].length) {
            return null;
        }
        return grid[y][x].platform;
    }

    public boolean isBlockAt(int x, int y, int z) {
        if(x < 0 || z < 0 || z >= grid.length || x >= grid[0].length) {
            return true;
        }
        GridItem gridItem = grid[z][x];
        return gridItem.top == -1 || y < gridItem.top || y >= gridItem.ceiling;
    }

    public Vector3i getBlockCoords(Vector3d pos) {
        int blockMinX = (int)Math.floor(pos.x / TILE_SIZE);
        int blockMinZ = (int)Math.floor(pos.z / TILE_SIZE);
        int blockMinY = (int) Math.floor(pos.y / TILE_SIZE);
        return new Vector3i(blockMinX, blockMinY, blockMinZ);
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
        if(!objectGroups.containsKey(group)) {
            objectGroups.put(group, new ArrayList<>());
        }
        return objectGroups.get(group);
    }

    public void buildLevel() {
        createDoors();
        createEntities();
        createLights();

        List<Vertex> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        int levelWidth = grid[0].length;
        int levelHeight = grid.length;

        for (int i = 0; i < levelHeight; i++) {
            for (int j = 0; j < levelWidth; j++) {
                GridItem item = grid[i][j];
                int top = item.top;

                if (top == -1) continue;

                boolean left = false,
                        right = false,
                        up = false,
                        down = false;

                if(j > 0 && Math.abs(grid[i][j - 1].top - top) == 0 && grid[i][j - 1].platform != item.platform) {
                    left = true;
                    edges.add(new Edge(i * levelWidth + j, i * levelWidth + j - 1, 1));
                }
                if(j < levelWidth - 1 && Math.abs(grid[i][j + 1].top - top) == 0 && grid[i][j + 1].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, i * levelWidth + j + 1, 1));
                    right = true;
                }
                if(i > 0 && Math.abs(grid[i - 1][j].top - top) == 0 && grid[i - 1][j].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j, 1));
                    up = true;
                }
                if(i < levelHeight - 1 && Math.abs(grid[i + 1][j].top - top) == 0 && grid[i + 1][j].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j, 1));
                    down = true;
                }

                if(left && up && Math.abs(grid[i - 1][j - 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j - 1, 1));
                }
                if(up && right && Math.abs(grid[i - 1][j + 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j + 1, 1));
                }
                if(right && down && Math.abs(grid[i + 1][j + 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j + 1, 1));
                }
                if(down && left && Math.abs(grid[i + 1][j - 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j - 1, 1));
                }

                if(left || right || up || down) {
                    vertices.add(new Vertex(i * levelWidth + j));
                }
            }
        }

        for(Vertex v : vertices) {
            int i = v.getId() / levelWidth;
            int j = v.getId() % levelWidth;
            GridItem item = grid[i][j];

            for(Vertex u : vertices) {
                int k = u.getId() / levelWidth;
                int l = u.getId() % levelWidth;

                if(v.getId() != u.getId() && grid[k][l].platform == item.platform) {
                    int d = (int)Math.sqrt(Math.pow(j - l, 2) + Math.pow(i - k, 2));
                    edges.add(new Edge(v.getId(), u.getId(), d));
                }
            }
        }

        dijkstra.buildGraph(vertices.toArray(new Vertex[0]), edges.toArray(new Edge[0]), true);
    }

    public GameObject getPlayer() {
        return player;
    }

    public Dijkstra getDijkstra() {
        return dijkstra;
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
                        gridItem.platform = platform;
                    }
                    if(gridItem.ceiling >= platform.ceiling) {
                        gridItem.ceiling = platform.ceiling;
                    }
                }
            }
        }
    }

    private void createEntities() {
        Scene scene = getGameObject().getScene();
        List<Entity> entities = levelMap.getEntities();
        for (Entity entity : entities) {
            LevelObject levelObject = getRegisteredObject(entity.name);
            GameObject go = new GameObject(scene);
            double y = getBlockTop((int)Math.floor(entity.x - levelMap.minX), (int)Math.floor(entity.y - levelMap.minY));
            Vector3d position = new Vector3d(entity.x - levelMap.minX, y, entity.y - levelMap.minY).mul(Level.TILE_SIZE);
            go.transform.position.set(position);
            Sprite sprite = new Sprite(levelObject.getSprite());
            sprite.setWidth((int)(Level.TILE_SIZE * levelObject.getScale().x));
            sprite.setHeight((int)(Level.TILE_SIZE * levelObject.getScale().y));
            SpriteRenderer renderer = new SpriteRenderer(sprite, -1);
            PathFinder pathFinder = new PathFinder(this);
            go.addComponent("PathFinder", pathFinder);
            if(!entity.name.equals("player")) {
                go.addComponent("Renderer", renderer);
                go.addComponent("Bilboard", new Bilboard());
            }
            switch (entity.name) {
                case "player": {
                    player = go;
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(1.5, 3, 1.5));
                    go.addComponent("Collider", collider);
                    Inventory inventory = new Inventory(this);
                    PlayerController playerController = new PlayerController(this, scene.getCamera(), collider, inventory, pathFinder);
                    go.addComponent("Controller", playerController);
                    go.addComponent("Inventory", inventory);
                    go.transform.position.y += collider.getSize().y / 2;

                    break;
                }
                case "cat_girl_1", "cat_girl_2", "cat_girl_3": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(1, 3, 1));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new CatGirlController(this, collider, dialogComponent, pathFinder));

                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "vending_machine": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2, 4, 2));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new VendingMachineController(this, collider, dialogComponent, pathFinder));
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "pc_desk", "drawer", "cupboard", "sink": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2.5, 2.5, 2.5));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new NpcController(this, collider, dialogComponent, pathFinder, 0));
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "ghost": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(1.5, 3, 1.5));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new NpcController(this, collider, dialogComponent, pathFinder, 0));
                    Animator animator = new Animator(renderer);
                    go.addComponent("Animator", animator);
                    animator.playAnim(AssetPool.getAnimation("untitled-game/animations/ghost__idle"), true);
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
            }
            if(levelObject.getType() == LevelObjectType.ITEM) {
                go.transform.scale.set(0.5);
                go.addComponent("Controller", new ItemController(this, entity.name));
                go.transform.position.y += sprite.getHeight() * levelObject.getScale().y;
            }

            scene.add(go);
        }
    }

    private void createLights() {
        Scene scene = getGameObject().getScene();
        List<Light> lights = levelMap.getLights();
        for (Light light : lights) {
            GameObject go = new GameObject(scene);
            go.transform.position.set(new Vector3d(light.x - levelMap.minX, light.top, light.y - levelMap.minY).mul(Level.TILE_SIZE));
            SpotLight spotLight = new SpotLight(new Color(light.color), light.radiusMin * Level.TILE_SIZE, light.radiusMax * Level.TILE_SIZE);
            go.addComponent("Light", spotLight);
            Sprite sprite = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png").getSprite(37);
            sprite.setWidth(1);
            sprite.setHeight(1);
            go.addComponent("Renderer", new SpriteRenderer(sprite, -1));
            go.addComponent("Bilboard", new Bilboard());
            scene.add(go);
        }

        GameObject ambientLightGameObject = new GameObject(scene);
        ambientLightGameObject.addComponent("AmbientLight", new AmbientLight(new Color(0.05, 0.05, 0.05)));
        scene.add(ambientLightGameObject);
    }

    private void createDoors() {
        Scene scene = getGameObject().getScene();
        List<Door> doors = levelMap.getDoors();
        for (Door door : doors) {
            GameObject doorGameObject = new GameObject(scene);
            MeshRenderer renderer = new MeshRenderer(new Cube(AssetPool.getTexture("untitled-game/images/door.png").getTexId()));
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

    private static class GridItem {
        private int top, ceiling;
        private Platform platform;

        public GridItem(int top, int ceiling) {
            this.top = top;
            this.ceiling = ceiling;
        }
    }
}
