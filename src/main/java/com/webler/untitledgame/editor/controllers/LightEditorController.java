package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Light;
import com.webler.untitledgame.level.levelmap.Serializable;
import com.webler.untitledgame.prefabs.editor.LightPrefab;
import org.joml.Vector2d;

import java.util.Locale;

public class LightEditorController extends EditorController {
    private final Light light;

    public LightEditorController(EditorComponent editorComponent, Light light) {
        super(editorComponent);
        this.light = light;
    }

    /**
    * Called by Imgui to set the values of the light to the editor's image ui. This is where the user can change the values
    */
    @Override
    public void editorImgui() {

        float[] x = {(float) light.getX()};
        Controls.floatControl("x", x, 0.1f);
        light.setX(x[0]);

        float[] y = {(float) light.getY()};
        Controls.floatControl("y", y, 0.1f);
        light.setY(y[0]);

        float[] top = {(float) light.getTop()};
        Controls.floatControl("top", top, 0.1f);
        light.setTop(top[0]);

        float[] radiusMin = {(float) light.getRadiusMin()};
        Controls.floatControl("radius min", radiusMin, 0.1f);
        light.setRadiusMin(radiusMin[0]);

        float[] radiusMax = {(float) light.getRadiusMax()};
        Controls.floatControl("radius max", radiusMax, 0.1f);
        light.setRadiusMax(radiusMax[0]);

        float[] color = light.getColor().toArray();
        Controls.colorPicker("color", color);
        light.setColor(Color.fromArray(color));

        float[] intensity = {(float) light.getIntensity()};
        Controls.floatControl("intensity", intensity, 0.01f, 0, 100);
        light.setIntensity(intensity[0]);
    }

    /**
    * Returns the light that this Light is associated with. This is useful for serializing objects that are shared between the Light and the Game.
    * 
    * 
    * @return the light that this Light is associated with or null if there is no light for this Game object ( such as when it is created
    */
    @Override
    public Serializable getSerializable() {
        return light;
    }

    /**
    * Synchronizes the position of the game object with the light. This is called when the light is added
    */
    @Override
    public void synchronize() {
        gameObject.transform.position.set(light.getX() * editorComponent.getConfig().gridWidth(), light.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    /**
    * Moves the light by the specified amount. This is called when the user drags the mouse over the component
    * 
    * @param transform - The transform of the object
    * @param start - The start position of the movement in world space
    * @param vector - The vector to move the light by in world
    */
    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        light.setX((transform.position.x + vector.x) / editorComponent.getConfig().gridWidth());
        light.setY((transform.position.y + vector.y) / editorComponent.getConfig().gridHeight());
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
    * Clones this prefab and returns a new instance of LightPrefab. Note that this method does not clone the game object itself.
    * 
    * 
    * @return a new instance of LightPrefab that is a copy of this prefab and has the same properties
    */
    @Override
    public GameObject clone() {
        return new LightPrefab(editorComponent, new Light(light.getX(), light.getY(), light.getTop(), light.getRadiusMin(), light.getRadiusMax(), light.getColor(), light.getIntensity())).create(gameObject.getScene());
    }

    /**
    * Returns a String representation of this Spotlight. The format is " Spotlight [ x = %. 3f y = %. 3f ] ".
    * 
    * 
    * @return a String representation of this Spotlight for debugging purposes or null if none could be found or if there was an error
    */
    @Override
    public String toString() {
        return String.format(Locale.US, "Spotlight [x=%.3f, y=%.3f]", light.getX(), light.getY());
    }
}
