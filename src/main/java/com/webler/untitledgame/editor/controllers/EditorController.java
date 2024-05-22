package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Serializable;
import org.joml.Vector2d;

public abstract class EditorController extends Component {
    protected EditorComponent editorComponent;
    protected Color color;

    public EditorController(EditorComponent editorComponent) {
        this.editorComponent = editorComponent;
        this.color = Color.WHITE;
    }

    /**
    * Called by JTextPane to indicate that it is the editor's Imgui. This is a no - op
    */
    public abstract void editorImgui();

    /**
    * Returns the serializable representation of this object. This is used to serialize and deserialise objects that are part of a transaction such as a JADE Transaction or an I / O object.
    * 
    * 
    * @return the serializable representation of this object or null if there is no serializable representation in the object's state
    */
    public abstract Serializable getSerializable();

    /**
    * Synchronizes the state of the persistence layer. This is called after #get () and only if the persistence layer is in the State#STATE_SCHEDULING state
    */
    public abstract void synchronize();

    /**
    * Moves the object to a new position. This is called by the WorldManger when it is about to move.
    * 
    * @param transform - the transform of the object being moved. Not used.
    * @param start - the new position of the object. Not used.
    * @param vector - the new vector of the object in world space
    */
    public abstract void move(Transform transform, Vector2d start, Vector2d vector);

    /**
    * Scales the object by the given amount. This is equivalent to calling transform. scale ( start vector ).
    * 
    * @param transform - the transform to be applied. Not null.
    * @param start - the scale to be applied. Not null.
    * @param vector - the scale to be applied. Not null. This is an optional parameter
    */
    public abstract void scale(Transform transform, Vector2d start, Vector2d vector);

    /**
    * Returns true if this item can be removed. This is used to determine if the user is removable or not.
    * 
    * 
    * @return true if this item can be removed false otherwise ( default is true ). Note that it is possible to remove items that are inaccessible
    */
    public boolean isRemovable() {
        return true;
    }

    /**
    * Returns true if this object can be cloned. The clone is a copy of the object that is passed to #clone ( Object ).
    * 
    * 
    * @return whether or not this object can be cloned ( true ) or not ( false ) by this object
    */
    public boolean isCloneable() {
        return true;
    }

    /**
    * Creates a copy of this GameObject. The clone is an in - place clone of the GameObject that is returned by this method.
    * 
    * 
    * @return a copy of this GameObject as a GameObject of the same type as this GameObject ( not a copy
    */
    public abstract GameObject clone();

    /**
    * Starts the synchronization. This is called by the thread that created the listener to ensure that it is running
    */
    @Override
    public void start() {
        synchronize();
    }

    /**
    * Updates the DebugDraw. This is called every frame to draw the debug draw. If you override this method be sure to call super. update
    * 
    * @param dt - Time since last update
    */
    @Override
    public void update(double dt) {
        synchronize();
        // This method is called by the renderer to draw the sprite.
        if(gameObject.hasComponent("Renderer")) {
            SpriteRenderer spriteRenderer = getComponent(SpriteRenderer.class, "Renderer");
            spriteRenderer.setColor(isSelected() ? Color.ORANGE : color);
            Rect boundingRect = spriteRenderer.getBoundingRect();
            DebugDraw.get().addRect(
                    new Vector2d(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2),
                    new Vector2d(boundingRect.width, boundingRect.height), Color.YELLOW);
        }
    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }

    /**
    * Checks if the game object is selected. This is used to prevent accidental deselection of the game object when editing a game that is not in the editor.
    * 
    * 
    * @return true if the game object is selected false otherwise ( in this case we don't want to deselect
    */
    private boolean isSelected() {
        return editorComponent.getSelectedGameObject() == getGameObject();
    }
}
