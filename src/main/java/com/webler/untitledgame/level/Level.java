package com.webler.untitledgame.level;

import com.webler.goliath.algorithm.Dijkstra;
import com.webler.goliath.algorithm.Edge;
import com.webler.goliath.algorithm.Vertex;
import com.webler.goliath.animation.components.Animator;
import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Fog;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.components.Billboard;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.controllers.entity.*;
import com.webler.untitledgame.level.controllers.entity.enemy.EnemyController;
import com.webler.untitledgame.level.controllers.entity.enemy.GoblinController;
import com.webler.untitledgame.level.controllers.entity.npc.KnightController;
import com.webler.untitledgame.level.controllers.entity.npc.VendingMachineController;
import com.webler.untitledgame.level.objects.LevelEntity;
import com.webler.untitledgame.level.objects.LevelItem;
import com.webler.untitledgame.level.objects.LevelObject;
import com.webler.untitledgame.level.enums.LevelObjectType;
import com.webler.untitledgame.level.ai.PathFinder;
import com.webler.untitledgame.level.controllers.*;
import com.webler.untitledgame.level.inventory.Inventory;
import com.webler.untitledgame.level.levelmap.*;
import com.webler.untitledgame.level.prefabs.ItemPrefab;
import lombok.Getter;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class Level extends Component {
    protected Logger logger = Logger.getLogger(Level.class.getName());
    public static final int TILE_SIZE = 4;
    @Getter
    private final LevelMap levelMap;
    private GridItem[][] grid;
    @Getter
    private String path;
    @Getter
    private GameObject player;
    private final Map<String, List<GameObject>> objectGroups;
    private final List<LevelObject> levelObjectRegistry;
    @Getter
    private final Dijkstra dijkstra;
    @Getter
    private boolean debug;

    public Level() {
        this.levelMap = new LevelMap();
        path = null;
        player = null;
        objectGroups = new HashMap<>();
        levelObjectRegistry = new ArrayList<>();
        dijkstra = new Dijkstra();
        debug = false;
        buildGrid();
    }

    /**
    * Returns the level object registered with the given identifier. If no match is found null is returned. This method is thread safe
    * 
    * @param identifier - the identifier of the level object to retrieve
    * 
    * @return the level object registered with the given identifier or null if no match is found ( for example if no object is registered
    */
    public LevelObject getRegisteredObject(String identifier) {
        return levelObjectRegistry.stream()
                .filter(item -> item.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }

    /**
    * Returns all level objects registered with the given type. This is useful for debugging and to avoid having to re - register every object that is registered in the same JVM
    * 
    * @param type - the type of the level object to search for
    * 
    * @return a list of all registered level objects of the given type or an empty list if no objects are registered
    */
    public List<LevelObject> getRegisteredObjects(LevelObjectType type) {
        return levelObjectRegistry.stream()
                .filter(item -> item.getType() == type)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
    * Initializes the level entities and level object registry. Must be called at the beginning of the game to avoid memory leaks
    */
    private void init() {
        Spritesheet tilesetSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png");
        Spritesheet ghostSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/ghost.png");
        Spritesheet knightSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/knight.png");
        Spritesheet playerSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/player.png");
        Spritesheet goblinSpritesheet = AssetPool.getSpritesheet("untitled-game/spritesheets/goblin.png");

        levelObjectRegistry.add(new LevelEntity("player", "Player",
                playerSpritesheet.getSprite(0), new Vector2d(0.75, 0.75), 25));
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
        levelObjectRegistry.add(new LevelEntity("knight", "Knight",
                knightSpritesheet.getSprite(0), new Vector2d(0.75, 0.75), 24));
        levelObjectRegistry.add(new LevelEntity("ghost", "Ghost",
                ghostSpritesheet.getSprite(0), new Vector2d(0.75, 0.75), 24));
        levelObjectRegistry.add(new LevelEntity("goblin", "Goblin",
                goblinSpritesheet.getSprite(0), new Vector2d(0.75, 0.75), 24));
        levelObjectRegistry.add(new LevelItem("ak47", "AK-47",
                new Sprite(AssetPool.getTexture("untitled-game/images/Ak47.png")), new Vector2d(0.8, 0.5), 50, "", 999));
        levelObjectRegistry.add(new LevelItem("shotgun", "Shotgun",
                new Sprite(AssetPool.getTexture("untitled-game/images/Shotgun.png")), new Vector2d(0.8, 0.5), 50, "", 999));
    }

    /**
    * Starts the service. This is called by the ServiceManager when it is ready to accept connections and will perform any initialization that is required
    */
    @Override
    public void start() {
        init();
    }

    /**
    * Updates the progress bar. This is called every frame to indicate the progress of the animation. The time in seconds since the last call to update () is given by dt
    * 
    * @param dt - the time since the last
    */
    @Override
    public void update(double dt) {
        if(Input.keyBeginPress(GLFW_KEY_X)) {
            debug = !debug;
        }
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Loads the level map from the specified file. This is useful for loading a file that is in the format used by the GUI.
    * 
    * @param fileName - Name of the file to load from. The file must be formatted as a path
    */
    public void load(String fileName) {
        levelMap.clear();
        try {
            levelMap.load(fileName);
            logger.info("Loaded level map from " + fileName);
        } catch (Exception e) {
            logger.warning("Failed to load level map from " + fileName);
            e.printStackTrace(System.err);
        }
        path = fileName;
        buildGrid();
    }

    /**
    * Saves the level map to the specified file. This is a convenience method that calls LevelMap#save ( String ) and sets the path to the file that was passed in
    * 
    * @param fileName - the name of the file to
    */
    public void save(String fileName) {
        try {
            levelMap.save(fileName);
            logger.info("Saved level map to " + fileName);
        } catch (Exception e) {
            logger.warning("Failed to save level map to " + fileName);
            e.printStackTrace(System.err);
        }
        path = fileName;
    }

    /**
    * Returns the top of the block at the x y position. If the block is empty 0 is returned
    * 
    * @param x - The x position of the block
    * @param y - The y position of the block ( 0 is top )
    * 
    * @return The top of the block at the x y position or 0 if there is no block at that position
    */
    public int getBlockTop(int x, int y) {
        // Returns the index of the first element in the grid.
        // Returns the index of the first element in the grid.
        if(x < 0 || y < 0 || y >= grid.length || x >= grid[0].length) {
            return 0;
        }
        return grid[y][x].top;
    }

    /**
    * Returns the platform of the block at the x y position. If the block is out of bounds null is returned
    * 
    * @param x - The x position of the block
    * @param y - The y position of the block ( top - left corner )
    * 
    * @return The platform of the block at the x y position or null if the block is outside the grid or the x y position is
    */
    public Platform getBlockPlatform(int x, int y) {
        // Returns null if the point is outside the grid.
        // Returns null if the point is outside the grid.
        if(x < 0 || y < 0 || y >= grid.length || x >= grid[0].length) {
            return null;
        }
        return grid[y][x].platform;
    }

    /**
    * Checks if the block at the x y z coordinates is on the grid. This is used to determine if we should block at the point of view in the game
    * 
    * @param x - The x coordinate of the point to check
    * @param y - The y coordinate of the point to check ( top - left )
    * @param z - The z coordinate of the point to check ( top - right )
    * 
    * @return True if the point is on the grid false otherwise ( in other words if there is no block at the point
    */
    public boolean isBlockAt(int x, int y, int z) {
        // Check if the grid is within the grid.
        // Check if the grid is within the grid.
        if(x < 0 || z < 0 || z >= grid.length || x >= grid[0].length) {
            return true;
        }
        GridItem gridItem = grid[z][x];
        return gridItem.top == -1 || y < gridItem.top || y >= gridItem.ceiling;
    }

    /**
    * Get the coordinates of the block that contains the given position. This is used to determine where the block should be drawn in the world
    * 
    * @param pos - The position of the block
    * 
    * @return The coordinates of the block in world coordinates or null if there is no block at that position ( in which case the block is not drawn
    */
    public Vector3i getBlockCoords(Vector3d pos) {
        int blockMinX = (int)Math.floor(pos.x / TILE_SIZE);
        int blockMinZ = (int)Math.floor(pos.z / TILE_SIZE);
        int blockMinY = (int) Math.floor(pos.y / TILE_SIZE);
        return new Vector3i(blockMinX, blockMinY, blockMinZ);
    }

    /**
    * Checks if any of the blocks in the specified box lie within the tiles. This is used to determine if a tile can be moved to a new tile
    * 
    * @param min - The x and y co - ordinates of the top left corner of the box
    * @param max - The x and y co - ordinates of the bottom left corner of the box
    * 
    * @return True if any of the blocks in the box lie within the tiles false otherwise. Note that the box is assumed to be in world
    */
    public boolean isBlockAtBox(Vector3d min, Vector3d max) {
        int blockMinX = (int)Math.floor(min.x / TILE_SIZE);
        int blockMinZ = (int)Math.floor(min.z / TILE_SIZE);
        int blockMaxX = (int)Math.floor(max.x / TILE_SIZE);
        int blockMaxZ = (int)Math.floor(max.z / TILE_SIZE);
        int blockMinY = (int) Math.floor(min.y / TILE_SIZE);
        int blockMaxY = (int) Math.floor(max.y / TILE_SIZE);
        // Returns true if any of the blocks in the block at the given coordinates.
        // Returns true if the block is at the specified coordinates.
        for(int z = blockMinZ; z <= blockMaxZ; ++z) {
            // Returns true if the block is at the specified coordinates.
            // Returns true if the given coordinates are block at the specified coordinates.
            for(int x = blockMinX; x <= blockMaxX; ++x) {
                // Returns true if the block is at the specified coordinates.
                // Returns true if the given coordinates are block at the given coordinates.
                for(int y = blockMinY; y <= blockMaxY; ++y) {
                    // Returns true if the given coordinates are block at the given coordinates.
                    // Returns true if the given coordinates are block at the given coordinates.
                    if(isBlockAt(x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
    * Adds an object to a group. This is useful for adding objects to a group that is in the game.
    * 
    * @param object - The object to add. Must not be null.
    * @param group - The name of the group to add the object to
    */
    public void addObjectToGroup(GameObject object, String group) {
        // Adds a group to the list of object groups.
        // Adds a group to the list of object groups.
        if(!objectGroups.containsKey(group)) {
            objectGroups.put(group, new ArrayList<>());
        }
        objectGroups.get(group).add(object);
    }

    /**
    * Removes the GameObject from the group. This is useful for removing an object from a group that is no longer in the game.
    * 
    * @param object - The GameObject to remove from the group.
    * @param group - The name of the group to remove the object from
    */
    public void removeObjectFromGroup(GameObject object, String group) {
        // Removes the object from the group.
        // Removes the object from the group.
        if(objectGroups.containsKey(group)) {
            objectGroups.get(group).remove(object);
        }
    }

    /**
    * Gets a list of GameObjects in the specified group. This is useful for debugging and to see which objects are in a group.
    * 
    * @param group - The name of the group to look up.
    * 
    * @return An array of GameObjects in the specified group or null if there are none in that group or if the group does not exist
    */
    public List<GameObject> getObjectsByGroup(String group) {
        // Adds a group to the list of object groups.
        // Adds a group to the list of object groups.
        if(!objectGroups.containsKey(group)) {
            objectGroups.put(group, new ArrayList<>());
        }
        return objectGroups.get(group);
    }

    /**
    * Builds the level. This is called after all objects have been added to the level but before it is added
    */
    public void buildLevel() {
        createDoors();
        createEntities();
        createLights();

        buildGraph();

        AudioManager.setGlobalGain(levelMap.getSoundVolume());

        //SoundSource bgMusic = AudioManager.createSoundSource(true);
        //bgMusic.play();
    }

    private void buildGraph() {
        List<Vertex> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();

        int levelWidth = grid[0].length;
        int levelHeight = grid.length;

        // Creates the edges for all the grid items in the grid.
        for (int i = 0; i < levelHeight; i++) {
            // Returns the edges of all grid items in the grid.
            for (int j = 0; j < levelWidth; j++) {
                GridItem item = grid[i][j];
                int top = item.top;

                // Skip the top of the stack.
                if (top == -1) continue;

                boolean left = false,
                        right = false,
                        up = false,
                        down = false;

                // left is left if the item is on the top of the grid
                if(j > 0 && Math.abs(grid[i][j - 1].top - top) == 0 && grid[i][j - 1].platform != item.platform) {
                    left = true;
                    edges.add(new Edge(i * levelWidth + j, i * levelWidth + j - 1, 1));
                }
                // Add an edge to the right edge
                if(j < levelWidth - 1 && Math.abs(grid[i][j + 1].top - top) == 0 && grid[i][j + 1].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, i * levelWidth + j + 1, 1));
                    right = true;
                }
                // Add an edge to the grid
                if(i > 0 && Math.abs(grid[i - 1][j].top - top) == 0 && grid[i - 1][j].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j, 1));
                    up = true;
                }
                // Add a new edge to the grid.
                if(i < levelHeight - 1 && Math.abs(grid[i + 1][j].top - top) == 0 && grid[i + 1][j].platform != item.platform) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j, 1));
                    down = true;
                }

                // Add an edge to the grid
                if(left && up && Math.abs(grid[i - 1][j - 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j - 1, 1));
                }
                // Add an edge to the grid
                if(up && right && Math.abs(grid[i - 1][j + 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i - 1) * levelWidth + j + 1, 1));
                }
                // Add an edge to the grid
                if(right && down && Math.abs(grid[i + 1][j + 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j + 1, 1));
                }
                // Add an edge to the grid
                if(down && left && Math.abs(grid[i + 1][j - 1].top - top) == 0) {
                    edges.add(new Edge(i * levelWidth + j, (i + 1) * levelWidth + j - 1, 1));
                }

                // Add a vertex to the list of vertices.
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

                // Add a new edge to the graph.
                if(v.getId() != u.getId() && grid[k][l].platform == item.platform) {
                    int d = (int)Math.sqrt(Math.pow(j - l, 2) + Math.pow(i - k, 2));
                    edges.add(new Edge(v.getId(), u.getId(), d));
                }
            }
        }

        dijkstra.buildGraph(vertices.toArray(new Vertex[0]), edges.toArray(new Edge[0]), true);
    }

    private void buildGrid() {
        int mapWidth = levelMap.getWidth();
        int mapHeight = levelMap.getHeight();
        grid = new GridItem[mapHeight][mapWidth];
        // Creates grid items for each level map
        for (int i = 0; i < mapHeight; i++) {
            // Creates grid items for each level map
            for (int j = 0; j < mapWidth; j++) {
                grid[i][j] = new GridItem(-1, levelMap.getCeiling());
            }
        }
        for(Platform platform : levelMap.getPlatforms()) {
            int x1, x2, y1, y2;
            x1 = platform.getX() - levelMap.getMinX();
            x2 = x1 + platform.getWidth() - 1;
            y1 = platform.getY() - levelMap.getMinY();
            y2 = y1 + platform.getHeight() - 1;

            // Find the top and ceiling of the grid items.
            for(int y = y1; y <= y2; ++y) {
                // Sets grid items top and ceiling.
                for(int x = x1; x <= x2; ++x) {
                    GridItem gridItem = grid[y][x];
                    // Set the top of the grid item
                    if(gridItem.top <= platform.getTop()) {
                        gridItem.top = platform.getTop();
                        gridItem.platform = platform;
                    }
                    // Set the ceiling of the grid item
                    if(gridItem.ceiling >= platform.getCeiling()) {
                        gridItem.ceiling = platform.getCeiling();
                    }
                }
            }
        }
    }

    // Creates entities and their game objects based on the level map. This is called by #createEntities ()
    /**
    * Creates entities and sprites for each entity in the level map. This is called from #createWorldObjects
    */
    private void createEntities() {
        Scene scene = getGameObject().getScene();
        List<Entity> entities = levelMap.getEntities();
        for (Entity entity : entities) {
            LevelObject levelObject = getRegisteredObject(entity.getName());

            double y = getBlockTop((int)Math.floor(entity.getX() - levelMap.getMinX()), (int)Math.floor(entity.getY() - levelMap.getMinY()));
            Vector3d position = new Vector3d(entity.getX() - levelMap.getMinX(), y, entity.getY() - levelMap.getMinY()).mul(Level.TILE_SIZE);

            // This method is called when the level object is an item.
            if(levelObject.getType() == LevelObjectType.ITEM) {
                GameObject go = new ItemPrefab(this, levelObject).create(scene);
                go.transform.position.set(position);
                go.transform.position.y += TILE_SIZE;
                scene.add(go);
                continue;
            }

            GameObject go = new GameObject(scene);

            go.transform.position.set(position);

            Sprite sprite = new Sprite(levelObject.getSprite());
            sprite.setWidth((int)(Level.TILE_SIZE * levelObject.getScale().x));
            sprite.setHeight((int)(Level.TILE_SIZE * levelObject.getScale().y));
            SpriteRenderer renderer = new SpriteRenderer(sprite, -1);

            PathFinder pathFinder = new PathFinder(this);
            go.addComponent("PathFinder", pathFinder);

            // Add the renderer and billboard components to the game.
            if(!entity.getName().equals("player")) {
                go.addComponent("Renderer", renderer);
                go.addComponent("Bilboard", new Billboard());
            }
            // The entity is the entity that is being used to create the entity.
            switch (entity.getName()) {
                case "player": {
                    player = go;
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2, 5, 2));
                    go.addComponent("Collider", collider);
                    Inventory inventory = new Inventory(this);
                    PlayerController playerController = new PlayerController(this, scene.getCamera(), collider, inventory, pathFinder);
                    go.addComponent("Controller", playerController);
                    go.addComponent("Inventory", inventory);
                    go.transform.position.y += collider.getSize().y / 2;
                    break;
                }
                case "knight": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2.5, 5, 2.5));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new KnightController(this, collider, dialogComponent, pathFinder));
                    Animator animator = new Animator(renderer);
                    go.addComponent("Animator", animator);
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    go.transform.scale.set(2, 2.5, 2);
                    renderer.offset.y = 0.25;
                    break;
                }
                case "vending_machine": {
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2.5, 6, 2.5));
                    go.addComponent("Collider", collider);
                    DialogComponent dialogComponent = new DialogComponent(getComponent(DialogManager.class, "DialogManager"));
                    go.addComponent("Controller", new VendingMachineController(this, collider, dialogComponent, pathFinder));
                    go.addComponent("Dialog", dialogComponent);
                    go.transform.position.y += collider.getSize().y / 2;
                    go.transform.scale.set(2);
                    renderer.offset.y = 0.5;
                    break;
                }
                case "ghost", "goblin": {
                    initEnemy(go, pathFinder, renderer, entity.getName());
                    break;
                }
            }

            scene.add(go);
        }
    }

    /**
    * Initializes enemy components and animator. Used to set up game objects that are part of the game.
    * 
    * @param go - GameObject to add components to. Can be null in which case it will be created on first access.
    * @param pathFinder - PathFinder to find paths to play when looking for objects
    * @param renderer - SpriteRenderer to use for animations
    * @param name - Name of the component
    */
    private void initEnemy(GameObject go, PathFinder pathFinder, SpriteRenderer renderer, String name) {
        BoxCollider3D collider = null;
        Animator animator = new Animator(renderer);
        go.addComponent("Animator", animator);
        // The name of the game.
        switch (name) {
            case "goblin": {
                collider = new BoxCollider3D(new Vector3d(2.5, 6, 2.5));
                go.addComponent("Controller", new GoblinController(this, collider,  pathFinder));
                go.transform.scale.set(2, 2.5, 2);
                break;
            }
            case "ghost": {
                collider = new BoxCollider3D(new Vector3d(2.5, 6, 2.5));
                go.addComponent("Controller", new EnemyController(this, collider,  pathFinder, 0));
                animator.playAnim(AssetPool.getAnimation("untitled-game/animations/ghost__idle"), true);
                go.transform.scale.set(2, 2.5, 2);
                break;
            }
        }
        go.addComponent("Collider", collider);
        if(collider != null)
            go.transform.position.y += collider.getSize().y / 2;
    }

    /**
    * Creates the lights for the level map. This is called by createWorld () when the game is loaded
    */
    private void createLights() {
        Scene scene = getGameObject().getScene();
        List<Light> lights = levelMap.getLights();
        for (Light light : lights) {
            GameObject go = new GameObject(scene);
            go.transform.position.set(new Vector3d(light.getX() - levelMap.getMinX(), light.getTop(), light.getY() - levelMap.getMinY()).mul(Level.TILE_SIZE));
            SpotLight spotLight = new SpotLight(new Color(light.getColor()), light.getRadiusMin() * Level.TILE_SIZE, light.getRadiusMax() * Level.TILE_SIZE);
            spotLight.setIntensity(light.getIntensity());
            go.addComponent("Light", spotLight);
            Sprite sprite = AssetPool.getSpritesheet("untitled-game/spritesheets/tileset.png").getSprite(37);
            sprite.setWidth(1);
            sprite.setHeight(1);
            go.addComponent("Renderer", new SpriteRenderer(sprite, -1));
            go.addComponent("Bilboard", new Billboard());
            scene.add(go);
        }

        Fog fog = gameObject.getGame().getRenderer().getFog();
        fog.fogNear = levelMap.getFogNear();
        fog.fogFar = levelMap.getFogFar();
        fog.fogColor = levelMap.getFogColor();

        GameObject ambientLightGameObject = new GameObject(scene);
        AmbientLight ambientLight = new AmbientLight(levelMap.getAmbeintColor());
        ambientLight.setIntensity(levelMap.getAmbientIntensity());
        ambientLightGameObject.addComponent("AmbientLight", ambientLight);
        scene.add(ambientLightGameObject);
    }

    /**
    * Creates doors and puts them in the scene. This is called by createWorld () when it is loaded
    */
    private void createDoors() {
        Scene scene = getGameObject().getScene();
        List<Door> doors = levelMap.getDoors();
        for (Door door : doors) {
            GameObject doorGameObject = new GameObject(scene);
            MeshRenderer renderer = new MeshRenderer(new Cube(AssetPool.getTexture("untitled-game/images/door.png").getTexId()));
            renderer.offset.x = 0.5;
            renderer.offset.y = -0.25;
            doorGameObject.addComponent("Renderer", renderer);
            double y = getBlockTop((door.getX() - levelMap.getMinX()), (door.getY() - levelMap.getMinY()));
            Vector3d position = new Vector3d(door.getX() - levelMap.getMinX() + 0.5, y + 1, door.getY() - levelMap.getMinY() + 0.5);
            // Move the door to the right or left direction.
            // Move the door to the right or left direction.
            switch (door.getDirection()) {
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
            doorGameObject.transform.scale.set(Level.TILE_SIZE, Level.TILE_SIZE * 1.5, Level.TILE_SIZE * 0.125);
            BoxCollider3D collider = new BoxCollider3D(new Vector3d());
            collider.offset.x = 0.5;
            collider.offset.y = 0.25;
            doorGameObject.addComponent("Collider", collider);
            doorGameObject.addComponent("Controller", new DoorController(this, collider, door.getDirection()));
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
