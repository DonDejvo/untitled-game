package com.webler.goliath.core;

import com.webler.goliath.Game;
import com.webler.goliath.graphics.components.Camera;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public abstract class Scene {
    @Getter
    private final Game game;
    private final List<GameObject> entities;
    private final LinkedList<GameObject> pendingEntities;
    private final LinkedList<GameObject> entitiesToRemove;
    private boolean running;
    @Setter
    @Getter
    private Camera camera;

    public Scene(Game game) {
        this.game = game;
        entities = new ArrayList<>();
        pendingEntities = new LinkedList<>();
        entitiesToRemove = new LinkedList<>();

        running = false;
        camera = null;
    }

    /**
    * Called when the scene is initialized. Subclasses should override this if they need to do something other than initializing the scene at the start of the game.
    * 
    * @param params - parameters that control the scene's initialisation
    */
    public abstract void init(SceneParams params);

    /**
    * Adds a GameObject to the list of entities. If the game is running it will be added to the pendingEntities list
    * 
    * @param e - The GameObject to add
    */
    public void add(GameObject e) {
        // Add a new entity to the list of entities.
        if(running) {
            pendingEntities.add(e);
        } else {
            entities.add(e);
        }
    }

    /**
    * Removes the GameObject from the list of entities to remove. If the game is running it will be added to the list of entities to remove
    * 
    * @param e - the GameObject to remove
    */
    public void remove(GameObject e) {
        // Remove the entity from the list of entities to be removed.
        if(running) {
            entitiesToRemove.add(e);
        } else {
            entities.remove(e);
        }
    }

    /**
    * Starts the camera. If it is already running it will be started and added to the list of cameras
    */
    public void start() {
        // Creates a new camera prefab and adds it to the camera prefab.
        if(camera == null) {
            GameObject cameraGameObject = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 1000).create(this);
            add(cameraGameObject);
        }
        running = true;
        for (GameObject e : entities) {
            e.registerListeners();
        }
        for (GameObject e : entities) {
            e.start();
        }
    }

    /**
    * Updates the game. This is called every frame to perform a physics update. Entities are added to the end of the list and removed from the list after they are started.
    * 
    * @param dt - Time since last frame in seconds ( ignored for performance
    */
    public void update(double dt) {
        for(GameObject e : entities) {
            e.update(dt);
        }
        // This method is used to add all pending entities to the pending entities queue.
        while(!pendingEntities.isEmpty()) {
            GameObject e = pendingEntities.poll();
            entities.add(e);
            e.registerListeners();
            e.start();
        }
        // Removes all entities from the list of entitiesToRemove.
        while(!entitiesToRemove.isEmpty()) {
            GameObject e = entitiesToRemove.poll();
            e.destroy();
            entities.remove(e);
        }
    }

    /**
    * Destroy the game. Called when the game is no longer needed to be destroyed or when an error occurs
    */
    public void destroy() {
        running = false;
        for (GameObject e : entities) {
            e.destroy();
        }
    }

    /**
    * Called when imgui is enabled in the scene. This is the place where you can add your own images
    */
    protected void sceneImgui() {
        for (GameObject e : entities) {
            e.imgui();
        }
    }

    /**
    * Called by ImageUI to display the image. This is a no - op if there is no image
    */
    public void imgui() {
        sceneImgui();
    }

    /**
    * Draws the game on the screen. This is called by the game's draw method to draw the
    */
    public void draw() {
        game.getFramebuffer().blitFramebuffer(game.getWidth(), game.getHeight());
    }

    /**
    * Returns a list of GameObjects that have been tagged with the given tag. This is useful for debugging purposes to determine if a game object is in the world or not
    * 
    * @param tag - The tag to search for
    * 
    * @return A list of GameObjects that have been tagged with the given tag or an empty list if there are
    */
    public List<GameObject> getEntitiesByTag(String tag) {
        return entities.stream().filter(e -> e.tags.contains(tag)).toList();
    }

    /**
    * Returns the GameObject with the given name. If no entity with the given name exists null is returned
    * 
    * @param name - Name of the entity to search for
    * 
    * @return GameObject with the given name or null if no entity with the given name exists in this game's
    */
    public GameObject getEntityByName(String name) {
        return entities.stream().filter(e -> e.getName().equals(name)).findAny().orElse(null);
    }
}
