package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Entity;
import com.webler.untitledgame.level.levelmap.Serializable;
import com.webler.untitledgame.prefabs.editor.EntityPrefab;
import org.joml.Vector2d;

import java.util.Locale;

public class EntityEditorController extends EditorController {
    private final Entity entity;

    public EntityEditorController(EditorComponent editorComponent, Entity entity) {
        super(editorComponent);
        this.entity = entity;
    }

    /**
    * Called by Imgui to set x and y values to 0. 1f. This is a hack to avoid drawing the image twice
    */
    @Override
    public void editorImgui() {

        float[] x = {(float) entity.getX()};
        Controls.floatControl("x", x, 0.1f);
        entity.setX(x[0]);

        float[] y = {(float) entity.getY()};
        Controls.floatControl("y", y, 0.1f);
        entity.setY(y[0]);
    }

    /**
    * Returns the entity that this entity represents. This is used to serialize / deserialise the entity when it is retrieved from the persistence layer.
    * 
    * 
    * @return the entity that this entity represents or null if there is no entity to represent for the purpose of serial
    */
    @Override
    public Serializable getSerializable() {
        return entity;
    }

    /**
    * Synchronizes the position of the game object with the position of the entity. This is called when the entity is moved
    */
    @Override
    public void synchronize() {
        gameObject.transform.position.set(entity.getX() * editorComponent.getConfig().gridWidth(), entity.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    /**
    * Moves the entity by the specified vector. This is called when the user drags the entity in response to a drag and drop.
    * 
    * @param transform - The transform of the entity. Not used.
    * @param start - The starting point of the move. Not used.
    * @param vector - The vector to move the entity by. Not used
    */
    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        entity.setX((transform.position.x + vector.x) / editorComponent.getConfig().gridWidth());
        entity.setY((transform.position.y + vector.y) / editorComponent.getConfig().gridHeight());
    }

    /**
    * Scales the object by the given amount. This is called by the scale method of the Transform object
    * 
    * @param transform - the transform to be scaled
    * @param start - the amount to scale in the x and y directions
    * @param vector - the amount to scale in the x and y
    */
    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {

    }

    /**
    * Creates a clone of this prefab. Note that this will not be possible to clone the Entity in any way.
    * 
    * 
    * @return a clone of this prefab with the same name and position as the original and a new GameObject
    */
    @Override
    public GameObject clone() {
        return new EntityPrefab(editorComponent, new Entity(entity.getName(), entity.getX(), entity.getY())).create(gameObject.getScene());
    }

    /**
    * Returns a string representation of this entity. The string is formatted as name [ x = %. 3f y = %. 3f ]
    * 
    * 
    * @return a string representation of this entity for debugging purposes ( not recommended for production use ) or null if there is no
    */
    @Override
    public String toString() {
        return String.format(Locale.US, "%s [x=%.3f, y=%.3f]", entity.getName(), entity.getX(), entity.getY());
    }
}
