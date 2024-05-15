package com.webler.goliath.core;

import com.webler.goliath.Game;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.core.exceptions.ComponentException;
import com.webler.goliath.eventsystem.EventManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public final class GameObject {
    private static final AtomicLong idsCounter = new AtomicLong(0);
    @Getter
    private final Game game;
    @Getter
    private final Scene scene;
    @Getter
    private final String name;
    private final Map<String, Component> components;
    public final Transform transform;
    public final Set<String> tags;

    public GameObject(Scene scene, String name) {
        this.scene = scene;
        this.game = scene.getGame();
        this.name = name;
        components = new HashMap<>();
        transform = new Transform();
        tags = new HashSet<>();
        addComponent("Transform", transform);
    }

    public GameObject(Scene scene) {
        this(scene, generateName());
    }

    public void registerListeners() {
        components.forEach((n, c) -> {
            EventManager.registerListeners(c);
        });
    }

    public void start() {
        components.forEach((n, c) -> {
            c.start();
        });
    }

    public void update(double dt) {
        components.forEach((n, c) -> {
            c.update(dt);
        });
    }

    public void destroy() {
        components.forEach((n, c) -> {
            c.destroy();
            EventManager.unregisterListeners(c);
        });
    }

    public void imgui() {
        components.forEach((n, c) -> {
            c.imgui();
        });
    }

    public void addComponent(String name, Component c) {
        if(components.containsKey(name)) {
            throw new ComponentException("Game object already contains component with name " + name);
        }
        c.setEntity(this);
        components.put(name, c);
    }

    public boolean hasComponent(String name) {
        return components.containsKey(name);
    }

    public <T extends Component> T getComponent(Class<T> cls, String name) {
        Component c = components.get(name);
        if(c == null) {
            throw new ComponentException("Game object " + this.name + " does not have component with name " + name + ".");
        }
        try {
            return cls.cast(c);
        } catch (ClassCastException e) {
            throw new ComponentException("Component " + name + " does not implement " + cls.getName() + " interface.");
        }
    }

    public void remove() {
        scene.remove(this);
    }

    private static String generateName() {
        return "__game_object__" + idsCounter.incrementAndGet();
    }
}
