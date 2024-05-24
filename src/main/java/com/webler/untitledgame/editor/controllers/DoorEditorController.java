package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.enums.Direction;
import com.webler.untitledgame.level.levelmap.Door;
import com.webler.untitledgame.level.levelmap.Serializable;
import com.webler.untitledgame.editor.prefabs.DoorPrefab;
import imgui.type.ImInt;
import org.joml.Vector2d;

import java.util.Arrays;

public class DoorEditorController extends EditorController {
    private final Door door;

    public DoorEditorController(EditorComponent editorComponent, Door door) {
        super(editorComponent);
        this.door = door;
    }

    /**
    * Called by ImGui to set controls and controls for door. This is where the magic happens. It's a bit messy but I don't know how to do it
    */
    @Override
    public void editorImgui() {

        int[] x = {door.getX()};
        Controls.intControl("x", x, 0.1f);
        door.setX(x[0]);

        int[] y = {door.getY()};
        Controls.intControl("y", y, 0.1f);
        door.setY(y[0]);

        String[] directions = Arrays.stream(Direction.values()).map(Enum::toString).toArray(String[]::new);
        int selectedDirectionIndex = 0;
        // Selects the direction of the door.
        for (int i = 0; i < directions.length; ++i) {
            // Set the index of the direction to the selected direction.
            if(directions[i].equals(door.getDirection().toString())) {
                selectedDirectionIndex = i;
            }
        }
        ImInt selectedDirection = new ImInt(selectedDirectionIndex);
        Controls.comboBox("direction", selectedDirection, directions);
        door.setDirection(Direction.valueOf(directions[selectedDirection.get()]));
    }

    /**
    * Returns door that this object is part of. This can be used to serialize / deserialise objects that are stored in the object store and retrieved by #getObject ( Object ).
    * 
    * 
    * @return door that this object is part of. This can be used to serialize / deserialize objects that are stored in the object store and retrieved by #getObject ( Object
    */
    @Override
    public Serializable getSerializable() {
        return door;
    }

    /**
    * Synchronizes door position with game object position. This is called when we have a change in door
    */
    @Override
    public void synchronize() {
        gameObject.transform.position.set(door.getX() * editorComponent.getConfig().gridWidth(), door.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    /**
    * Moves the door. This is called every frame by Unity's event dispatch thread. The implementation of this method does nothing
    * 
    * @param transform - the transform of the event
    * @param start - the start position of the event ( not used )
    * @param vector - the vector to move the door ( not used
    */
    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        door.setX((int) (Math.floor(0.5 + (transform.position.x + vector.x) / editorComponent.getConfig().gridWidth())));
        door.setY((int) (Math.floor(0.5 + (transform.position.y + vector.y) / editorComponent.getConfig().gridHeight())));
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
    * Creates a clone of this door prefab. Note that this does not clone the scene it just clones the door's position and direction.
    * 
    * 
    * @return a clone of this door prefab with the same position and direction as the original door and
    */
    @Override
    public GameObject clone() {
        return new DoorPrefab(editorComponent, new Door(door.getX(), door.getY(), door.getDirection())).create(gameObject.getScene());
    }

    /**
    * Returns a String representation of this Door. This is useful for debugging purposes. The String representation will look like : Door [ x = x y = y ]
    * 
    * 
    * @return a String representation of this Door ( debugging is disabled by default but can be enabled by setting #isDebugEnabled () to true
    */
    @Override
    public String toString() {
        return "Door [x=" + door.getX() + ", y=" + door.getY() + "]";
    }
}
