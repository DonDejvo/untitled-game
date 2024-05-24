package com.webler.untitledgame.editor;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.FileBrowser;
import com.webler.goliath.graphics.widgets.FileBrowserAction;
import com.webler.goliath.input.Input;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.editor.controllers.EditorController;
import com.webler.untitledgame.editor.windows.HierarchyWindow;
import com.webler.untitledgame.editor.windows.InspectorWindow;
import com.webler.untitledgame.editor.windows.LevelWindow;
import com.webler.untitledgame.level.enums.Direction;
import com.webler.untitledgame.level.levelmap.*;
import com.webler.untitledgame.editor.prefabs.DoorPrefab;
import com.webler.untitledgame.editor.prefabs.EntityPrefab;
import com.webler.untitledgame.editor.prefabs.PlatformPrefab;
import com.webler.untitledgame.editor.prefabs.LightPrefab;
import com.webler.untitledgame.scenes.LevelParams;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2d;
import org.joml.Vector4d;


import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class EditorComponent extends Component {
    public static final String SELECTABLE_TAG = "selectable";
    @Getter
    private final Level level;
    private MenuBar menuBar;
    private HierarchyWindow hierarchyWindow;
    private LevelWindow levelWindow;
    private InspectorWindow inspectorWindow;
    @Getter
    private final EditorConfig config;
    @Getter
    private String currentPath;
    @Getter
    private GameObject selectedGameObject;
    @Setter
    @Getter
    private boolean hierarchyWindowOpened;
    @Setter
    @Getter
    private boolean levelWindowOpened;
    @Setter
    @Getter
    private boolean inspectorWindowOpened;
    private FileBrowserAction fileBrowserAction;
    private final Vector2d mouseBeginPosition;
    private final Vector2d mousePosition;
    private Transform selectedObjectTransform;
    private MouseAction mouseAction;

    public EditorComponent(Level level, EditorConfig config, String currentPath) {
        this.level = level;
        this.config = config;
        this.currentPath = currentPath;
        selectedGameObject = null;
        hierarchyWindowOpened = true;
        levelWindowOpened = true;
        inspectorWindowOpened = true;
        fileBrowserAction = FileBrowserAction.NONE;
        mouseBeginPosition = new Vector2d();
        mousePosition = new Vector2d();
        selectedObjectTransform = null;
        mouseAction = MouseAction.MOVE;
    }
    
    /**
    * Starts the application. This is called by JNI when the application is started but not when it is resumed
    */
    @Override
    public void start() {
        menuBar = new MenuBar(this);
        hierarchyWindow = new HierarchyWindow(this);
        levelWindow = new LevelWindow(this);
        inspectorWindow = new InspectorWindow(this);
        // Loads the current level of the current path.
        if(currentPath != null) {
            loadLevel(currentPath);
        }
    }

    /**
    * Updates the state of the controller. This is called every frame to determine if it should be updated or not
    * 
    * @param dt - time since the last
    */
    @Override
    public void update(double dt) {
        handleInput();
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Called when the user presses the Image button. Opens the FileBrowser if it is open and saves the level
    */
    @Override
    public void imgui() {

        menuBar.imgui();

        // This method is called when the level window is opened.
        if(levelWindowOpened) {
            levelWindow.imgui();
        }

        // Called when the hierarchy window is opened.
        if(hierarchyWindowOpened) {
            hierarchyWindow.imgui();
        }

        // Called when the inspector window is opened.
        if(inspectorWindowOpened) {
            inspectorWindow.imgui();
        }

        // Opens the file browser.
        switch (fileBrowserAction) {
            case OPEN:
                FileBrowser.open(FileBrowserAction.OPEN);
                break;
            case SAVE:
                FileBrowser.open(FileBrowserAction.SAVE);
        }
        fileBrowserAction = FileBrowserAction.NONE;

        // Loads the current result path and saves the current path to the result path.
        if(FileBrowser.getModal()) {
            // Loads the result path from the result path.
            if(FileBrowser.getResultPath() != null) {
                // Loads the current path and saves the current level.
                switch (FileBrowser.getAction()) {
                    case OPEN: {
                        String path = FileBrowser.getResultPath();
                        try {
                            loadLevel(path);
                            currentPath = path;
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                        break;
                    }
                    case SAVE: {
                        String path = FileBrowser.getResultPath();
                        saveLevel(path);
                        currentPath = path;
                        break;
                    }
                }

            }
        }
    }

    /**
    * Handles the play event. Saves the current level and plays the level's scene if it's
    */
    public void handlePlay() {
        // Save the current path to the scene.
        if(currentPath != null) {
            saveLevel(currentPath);
            getGameObject().getGame().playScene("LevelScene", new LevelParams(currentPath));
        }
    }

    /**
    * Opens the file browser. This method is called when the user clicks the open button in the FileBrowser
    */
    public void handleOpen() {
        fileBrowserAction = FileBrowserAction.OPEN;
    }

    /**
    * Called when a new node is encountered. Clears the current level and sets the currentPath to null. This is useful for the start of a new tree
    */
    public void handleNew() {
        currentPath = null;
        clearLevel();
    }

    /**
    * Saves the current level to the file specified by the currentPath property. If there is no currentPath the FileBrowserAction#SAVE SAVE is
    */
    public void handleSave() {
        // Save the current path to the current path.
        if(currentPath == null) {
            fileBrowserAction = FileBrowserAction.SAVE;
        } else {
            saveLevel(currentPath);
        }
    }

    /**
    * Saves the current file to the user's computer. This is a no - op if the user cancels
    */
    public void handleSaveAs() {
        fileBrowserAction = FileBrowserAction.SAVE;
    }

    /**
    * Adds a platform to the game and selects it. This is called when the player clicks on the platform
    */
    public void addPlatform() {
        Scene scene = getGameObject().getScene();
        int x = (int)(scene.getCamera().getGameObject().transform.position.x / config.gridWidth());
        int y = (int)(scene.getCamera().getGameObject().transform.position.y / config.gridHeight());
        GameObject platformGameObject = new PlatformPrefab(this,
                new Platform(x, y, 1, 1, 0, level.getLevelMap().getCeiling())).create(scene);
        scene.add(platformGameObject);
        selectGameObject(platformGameObject);
    }

    /**
    * Adds a spot light to the game object. It is used to highlight the spot when the player clicks
    */
    public void addSpotLight() {
        Scene scene = getGameObject().getScene();
        double x = scene.getCamera().getGameObject().transform.position.x / config.gridWidth();
        double y = scene.getCamera().getGameObject().transform.position.y / config.gridHeight();
        GameObject spotLightGameObject = new LightPrefab(this,
                new Light(x, y, 0.5, 5, 10, Color.WHITE, 1)).create(scene);
        scene.add(spotLightGameObject);
        selectGameObject(spotLightGameObject);
    }

    /**
    * Adds an entity to the game. This will be displayed in the grid as well as at the position of the player
    * 
    * @param name - The name of the
    */
    public void addEntity(String name) {
        Scene scene = getGameObject().getScene();
        double x = scene.getCamera().getGameObject().transform.position.x / config.gridWidth();
        double y = scene.getCamera().getGameObject().transform.position.y / config.gridHeight();
        GameObject entity = new EntityPrefab(this,
                new Entity(name, x, y)).create(scene);
        scene.add(entity);
        selectGameObject(entity);
    }

    /**
    * Adds a door to the game object and selects it. This is called when the player clicks on the door
    */
    public void addDoor() {
        Scene scene = getGameObject().getScene();
        int x = (int)(scene.getCamera().getGameObject().transform.position.x / config.gridWidth());
        int y = (int)(scene.getCamera().getGameObject().transform.position.y / config.gridHeight());
        GameObject door = new DoorPrefab(this,
                new Door(x, y, Direction.DOWN)).create(scene);
        scene.add(door);
        selectGameObject(door);
    }

    /**
    * Selects the GameObject to play. This is called by the game object when it is selected. If you don't want to play the game object you can call this method with null as the parameter
    * 
    * @param gameObject - The GameObject to play
    */
    public void selectGameObject(GameObject gameObject) {
        selectedGameObject = gameObject;
    }

    /**
    * Loads and reorganizes the level. This is called when the level is loaded from a file.
    * 
    * @param fileName - The name of the file to load the level
    */
    private void loadLevel(String fileName) {
        Scene scene = gameObject.getScene();
        try {
            level.load(fileName);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        clearLevel();

        List<Platform> platforms = level.getLevelMap().getPlatforms();
        for(Platform platform : platforms) {
            GameObject platformGameObject = new PlatformPrefab(this, platform).create(scene);
            scene.add(platformGameObject);
        }

        List<Light> lights = level.getLevelMap().getLights();
        for(Light light : lights) {
            GameObject lightGameObject = new LightPrefab(this, light).create(scene);
            scene.add(lightGameObject);
        }

        List<Entity> entities = level.getLevelMap().getEntities();
        for(Entity entity : entities) {
            GameObject entityGameObject = new EntityPrefab(this, entity).create(scene);
            scene.add(entityGameObject);
        }

        List<Door> doors = level.getLevelMap().getDoors();
        for(Door door : doors) {
            GameObject doorGameObject = new DoorPrefab(this, door).create(scene);
            scene.add(doorGameObject);
        }

        Camera camera = gameObject.getScene().getCamera();
        LevelMap levelMap = level.getLevelMap();
        camera.getGameObject().transform.position.x = levelMap.getCameraX();
        camera.getGameObject().transform.position.y = levelMap.getCameraY();
    }

    /**
    * Saves the level to file. This is done by iterating through all objects that are in the level and adding them to the level.
    * 
    * @param fileName - Name of the file to save to. If null the file will be saved to the current directory
    */
    private void saveLevel(String fileName) {
        Scene scene = gameObject.getScene();

        level.getLevelMap().clear();

        List<GameObject> platformObjects = scene.getEntitiesByTag(Platform.TAG);
        for(GameObject platformObject : platformObjects) {
            Platform platform = (Platform) platformObject
                    .getComponent(EditorController.class, "Controller")
                    .getSerializable();
            level.getLevelMap().addPlatform(platform);
        }

        List<GameObject> lightObjects = scene.getEntitiesByTag(Light.TAG);
        for(GameObject lightObject : lightObjects) {
            Light light = (Light) lightObject
                    .getComponent(EditorController.class, "Controller")
                    .getSerializable();
            level.getLevelMap().addLight(light);
        }

        List<GameObject> entityObjects = scene.getEntitiesByTag(Entity.TAG);
        for(GameObject entityObject : entityObjects) {
            Entity entity = (Entity) entityObject
                    .getComponent(EditorController.class, "Controller")
                    .getSerializable();
            level.getLevelMap().addEntity(entity);
        }

        List<GameObject> doorObjects = scene.getEntitiesByTag(Door.TAG);
        for(GameObject doorObject : doorObjects) {
            Door door = (Door) doorObject
                    .getComponent(EditorController.class, "Controller")
                    .getSerializable();
            level.getLevelMap().addDoor(door);
        }

        level.save(fileName);
    }

    /**
    * Handles input and updates the level map. This is called every frame from GameObject. onGLFW
    */
    private void handleInput() {
        LevelMap levelMap = level.getLevelMap();
        Camera camera = gameObject.getScene().getCamera();
        Vector2d worldMousePosition = getWorldMousePosition();

        // This method is called when the mouse button is pressed.
        if(Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT) ||
                Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_RIGHT)
        ) {
            mouseBeginPosition.set(worldMousePosition);
        }
        mousePosition.set(worldMousePosition);

        // This method is called when the mouse button is pressed.
        if(Input.mouseButtonPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
            camera.getGameObject().transform.position.x -= mousePosition.x - mouseBeginPosition.x;
            camera.getGameObject().transform.position.y -= mousePosition.y - mouseBeginPosition.y;
            levelMap.setCameraX(camera.getGameObject().transform.position.x);
            levelMap.setCameraY(camera.getGameObject().transform.position.y);
        }

        GameObject hoveredGameObject = getHoveredGameObject();
        // This method is called when the mouse button is pressed.
        if(Input.mouseButtonBeginPress(GLFW_MOUSE_BUTTON_LEFT)) {

            selectedGameObject = hoveredGameObject;
            // This method is called when the user presses the scale or move button.
            if(selectedGameObject != null) {
                selectedObjectTransform = new Transform(selectedGameObject.transform);
                // Scale or move depending on key presses
                if(Input.keyPressed(GLFW_KEY_S)) {
                    mouseAction = MouseAction.SCALE;
                } else {
                    mouseAction = MouseAction.MOVE;
                }
            }
        }

        // This method is called when the user clicks on the selected game object.
        if(selectedGameObject != null) {
            // Returns true if the mouse button is pressed.
            if(Input.mouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT)) {
                // The action to be performed on the mouse.
                switch(mouseAction) {
                    case SCALE:
                        scaleSelectedObject();
                        break;
                    case MOVE:
                        moveSelectedObject();
                        break;
                }
            }
        }
    }

    /**
    * Moves the selected object to the mouse begin position. This is called by moveMouse () when the mouse is moved
    */
    private void moveSelectedObject() {
        // Moves the selected Game Object to the current position.
        if(selectedGameObject != null) {
            Vector2d vector = getWorldMousePosition().sub(mouseBeginPosition);
            selectedGameObject.getComponent(EditorController.class, "Controller").move(selectedObjectTransform, mouseBeginPosition, vector);
        }
    }

    /**
    * Scales the selected object to the mouse position. This is called by scale () when the mouse is moved
    */
    private void scaleSelectedObject() {
        // Scale the selectedGameObject to the current mouse position.
        if(selectedGameObject != null) {
            Vector2d vector = getWorldMousePosition().sub(mouseBeginPosition);
            selectedGameObject.getComponent(EditorController.class, "Controller").scale(selectedObjectTransform, mouseBeginPosition, vector);
        }
    }

    /**
    * Finds the GameObject that the mouse is hovering over. This is used to determine if the mouse is over a SpriteRenderer or not.
    * 
    * 
    * @return The prefab or null if none is over the selection or there is no prefab at the mouse
    */
    private GameObject getHoveredGameObject() {
        Vector2d worldMousePosition = getWorldMousePosition();
        List<GameObject> selectableObjects = gameObject.getScene().getEntitiesByTag(SELECTABLE_TAG);
        GameObject hoveredGameObject = null;
        int zIndex = 0;
        for(GameObject go : selectableObjects) {
            SpriteRenderer renderer = go.getComponent(SpriteRenderer.class, "Renderer");
            // If the game object is hovered by the world mouse position in the bounds of the renderer and the mouse position is not in the bounds of the world.
            if(renderer.getBoundingRect().contains(worldMousePosition) && (hoveredGameObject == null || zIndex <= renderer.getZIndex())) {
                hoveredGameObject = go;
                zIndex = renderer.getZIndex();
            }
        }
        return hoveredGameObject;
    }

    /**
    * Gets the position of the mouse relative to the world. This is used to determine where the mouse is in the world when moving to a position that is the same as the mouse position.
    * 
    * 
    * @return The position of the mouse relative to the world ( 0 0 ) and positive z - axis ( 1
    */
    private Vector2d getWorldMousePosition() {
        Camera camera = gameObject.getScene().getCamera();
        Rect levelViewport = levelWindow.getLevelViewport();
        Vector4d v = new Vector4d((Input.mouseX() - levelViewport.x) / levelViewport.width * 2 - 1,
                (1 - (Input.mouseY() - levelViewport.y) / levelViewport.height) * 2 - 1, 0.0, 1.0);
        v.mul(camera.getInversePVMatrix());
        return new Vector2d(v.x, v.y);
    }

    /**
    * Clears the level. This is called when the player clicks the clear button in the game and it's time to go back to the start
    */
    private void clearLevel() {
        Scene scene = gameObject.getScene();

        List<GameObject> platformsToRemove = scene.getEntitiesByTag(Platform.TAG);
        for(GameObject platform : platformsToRemove) {
            scene.remove(platform);
        }

        List<GameObject> lightsToRemove = scene.getEntitiesByTag(Light.TAG);
        for(GameObject light : lightsToRemove) {
            scene.remove(light);
        }

        List<GameObject> entitiesToRemove = scene.getEntitiesByTag(Entity.TAG);
        for(GameObject entity : entitiesToRemove) {
            scene.remove(entity);
        }

        List<GameObject> doorsToRemove = scene.getEntitiesByTag(Door.TAG);
        for(GameObject door : doorsToRemove) {
            scene.remove(door);
        }
    }

    private enum MouseAction {
        MOVE, SCALE
    }
}
