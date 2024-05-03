package com.webler.goliath.core;

import com.webler.goliath.Game;
import com.webler.goliath.graphics.components.Camera;

import java.util.*;

public abstract class Scene {
    private final Game game;
    private final List<GameObject> entities;
    private final LinkedList<GameObject> pendingEntities;
    private final LinkedList<GameObject> entitiesToRemove;
    private boolean running;
    private Camera camera;

    public Scene(Game game) {
        this.game = game;
        entities = new ArrayList<>();
        pendingEntities = new LinkedList<>();
        entitiesToRemove = new LinkedList<>();

        running = false;
        camera = null;
    }

    public abstract void init(SceneParams params);

    public void add(GameObject e) {
        if(running) {
            pendingEntities.add(e);
        } else {
            entities.add(e);
        }
    }

    public void remove(GameObject e) {
        if(running) {
            entitiesToRemove.add(e);
        } else {
            entities.remove(e);
        }
    }

    public void start() {
        if(camera == null) {
            // TODO: Create own exception class
            throw new RuntimeException("Camera is not set in scene " + game.getCurrentSceneName());
        }
        running = true;
        for (GameObject e : entities) {
            e.registerListeners();
        }
        for (GameObject e : entities) {
            e.start();
        }
    }

    public void update(double dt) {
        for(GameObject e : entities) {
            e.update(dt);
        }
        while(!pendingEntities.isEmpty()) {
            GameObject e = pendingEntities.poll();
            entities.add(e);
            e.registerListeners();
            e.start();
        }
        while(!entitiesToRemove.isEmpty()) {
            GameObject e = entitiesToRemove.poll();
            e.destroy();
            entities.remove(e);
        }
    }

    public void destroy() {
        running = false;
        for (GameObject e : entities) {
            e.destroy();
        }
    }

    protected void sceneImgui() {
        for (GameObject e : entities) {
            e.imgui();
        }
    }

    public void imgui() {
        sceneImgui();
    }

    public void draw() {
        game.getFramebuffer().blitFramebuffer(game.getWidth(), game.getHeight());
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Game getGame() {
        return game;
    }

    public List<GameObject> getEntitiesByTag(String tag) {
        return entities.stream().filter(e -> e.tags.contains(tag)).toList();
    }

    public GameObject getEntityByName(String name) {
        return entities.stream().filter(e -> e.getName().equals(name)).findAny().orElse(null);
    }
}
