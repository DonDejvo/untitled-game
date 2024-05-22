package com.webler.goliath.core;

import com.webler.goliath.Game;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.core.exceptions.ComponentNotFoundException;
import com.webler.goliath.core.exceptions.ComponentTypeException;
import com.webler.goliath.eventsystem.EventManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public final class GameObject {
    private final Logger logger = Logger.getLogger(GameObject.class.getName());
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

    /**
    * Registers all listeners for this component. This is called by #init ( Component ) and should be called before any components are added
    */
    public void registerListeners() {
        components.forEach((n, c) -> EventManager.registerListeners(c));
    }

    /**
    * Start the component. This is called by the start method of each component that is responsible for processing the data
    */
    public void start() {
        components.forEach((n, c) -> c.start());
    }

    /**
    * Updates the state of all components. This is called every time a timestep is updated. The time since the last call to this method is given in seconds and may be used to determine when to stop the simulation.
    * 
    * @param dt - the time since the last update in seconds or
    */
    public void update(double dt) {
        components.forEach((n, c) -> c.update(dt));
    }

    /**
    * Destroy all components and unregisters all event listeners. This is called when the application is shut down and should not be used
    */
    public void destroy() {
        components.forEach((n, c) -> {
            c.destroy();
            EventManager.unregisterListeners(c);
        });
    }

    /**
    * Draws the image UI for all components in this scene. This is a no - op if there are no components
    */
    public void imgui() {
        components.forEach((n, c) -> c.imgui());
    }

    /**
    * Adds a component to the GameObject. This will be used to determine if an entity is in charge of the component
    * 
    * @param name - The name of the component
    * @param c - The component to add to the GameObject as
    */
    public void addComponent(String name, Component c) {
        // Check if game object contains a component with the given name
        if(components.containsKey(name)) {
            logger.warning("Game object already contains component with name " + name);
        }
        c.setEntity(this);
        components.put(name, c);
    }

    /**
    * Returns true if this component has a component with the given name. This is useful for determining if a component is in use and can be used to create a Component object that represents the component.
    * 
    * @param name - the name of the component to look for.
    * 
    * @return whether or not there is a component with the given name in this component set ( false if not
    */
    public boolean hasComponent(String name) {
        return components.containsKey(name);
    }

    /**
    * Gets the component with the specified name. This method is useful for components that have been added to a component group by an external tool such as Jitsi Gradle or other application - specific code.
    * 
    * @param cls - The class of the component to retrieve. This must be a subclass of Component.
    * @param name - The name of the component to retrieve. This must be a fully - qualified name ( including the namespace ).
    * 
    * @return The component with the specified name or null if there is no such component in the group or if the component does not exist
    */
    public <T extends Component> T getComponent(Class<T> cls, String name) {
        Component c = components.get(name);
        // Throws an exception if the component is not found.
        if(c == null) {
            throw new ComponentNotFoundException(this.name, name);
        }
        try {
            return cls.cast(c);
        } catch (ClassCastException e) {
            throw new ComponentTypeException(name, cls.getName());
        }
    }

    /**
    * Removes this Scene from the scene's list of Scene objects. This is called when the Scene is no longer needed
    */
    public void remove() {
        scene.remove(this);
    }

    /**
    * Generates a name for an object. This is used to distinguish objects that are in the same game as each other.
    * 
    * 
    * @return The name that should be used for the object ( without prefix ). Note that the name may be different from the id
    */
    private static String generateName() {
        return "__game_object__" + idsCounter.incrementAndGet();
    }
}
