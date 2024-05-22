package com.webler.goliath.core;

import lombok.Getter;
import org.joml.Vector3d;

public abstract class Component {
    @Getter
    protected GameObject gameObject;
    public final Vector3d offset;

    public Component() {
        offset = new Vector3d();
    }

    /**
    * Starts the service. This method is called by the Service class when it starts its processing loop. Subclasses should override this method
    */
    public abstract void start();

    /**
    * Updates the state of the particle. This is called every frame to indicate that the particle has changed and should be redrawn.
    * 
    * @param dt - the time since the last update in seconds ( may be 0. 0
    */
    public abstract void update(double dt);

    /**
    * Called when the object is no longer needed. This is the place to do any cleanup that needs to be done at the end of the
    */
    public abstract void destroy();

    /**
    * This method is called by ImageUI. doPaint () to do any drawing that is required before the image is drawn
    */
    public void imgui() {}

    /**
    * Sets the entity that this entity is associated with. This is used to create a GameObject for the entity in the game.
    * 
    * @param gameObject - The game object to associate with this entity
    */
    public void setEntity(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    /**
    * Gets a component by class and name. This is useful for looking up components that are part of a game object.
    * 
    * @param cls - The class of the component to get. Must be a subclass of Component.
    * @param name - The name of the component to get. If name is null or " " the name will be determined automatically from cls.
    * 
    * @return The component or null if not found or not found in the game object's list of components. Note that null is returned if there is no component with the given
    */
    public <T extends Component> T getComponent(Class<T> cls, String name) {
        return gameObject.getComponent(cls, name);
    }

    /**
    * Returns the offset position of the game object. This is used to determine where the object should be positioned relative to the camera.
    * 
    * 
    * @return The offset position of the game object relative to the camera's position ( in world coordinates ). Note that the offset is calculated by multiplying the offset with the camera's scale
    */
    public Vector3d getOffsetPosition() {
        return new Vector3d(offset)
                .mul(gameObject.transform.scale)
                .rotate(gameObject.transform.rotation)
                .add(gameObject.transform.position);
    }
}
