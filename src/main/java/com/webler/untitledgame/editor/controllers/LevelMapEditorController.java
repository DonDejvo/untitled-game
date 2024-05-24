package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.enums.Environment;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.type.ImInt;
import org.joml.Vector2d;

import java.util.Arrays;

public class LevelMapEditorController extends EditorController {
    private final Level level;

    public LevelMapEditorController(EditorComponent editorComponent, Level level) {
        super(editorComponent);
        this.level = level;
    }

    /**
    * Called by ImGui to add controls to the level editor. This is a bit hacky but I don't know how to get it working
    */
    @Override
    public void editorImgui() {
        LevelMap levelMap = level.getLevelMap();

        int[] top = {levelMap.getCeiling()};
        Controls.intControl("Ceiling", top, 0.1f, 1, 100);
        levelMap.setCeiling(top[0]);

        String[] environments = Arrays.stream(Environment.values()).map(Enum::toString).toArray(String[]::new);
        int selectedEnvironmentIndex = 0;
        // Selects the currently selected environment.
        for (int i = 0; i < environments.length; ++i) {
            // Selects the current selected environment.
            if(environments[i].equals(levelMap.getEnvironment().toString())) {
                selectedEnvironmentIndex = i;
            }
        }
        ImInt selectedEnvironment = new ImInt(selectedEnvironmentIndex);
        Controls.comboBox("environment", selectedEnvironment, environments);
        levelMap.setEnvironment(Environment.valueOf(environments[selectedEnvironment.get()]));

        float[] fogNear = {(float) levelMap.getFogNear()};
        Controls.floatControl("fog near", fogNear, 0.1f);
        levelMap.setFogNear(fogNear[0]);

        float[] fogFar = {(float) levelMap.getFogFar()};
        Controls.floatControl("fog far", fogFar, 0.1f);
        levelMap.setFogFar(fogFar[0]);

        float[] fogColor = levelMap.getFogColor().toArray();
        Controls.colorPicker("fog color", fogColor);
        levelMap.setFogColor(Color.fromArray(fogColor));

        float[] color = levelMap.getAmbeintColor().toArray();
        Controls.colorPicker("ambient light color", color);
        levelMap.setAmbeintColor(Color.fromArray(color));

        float[] intensity = {(float) levelMap.getAmbientIntensity()};
        Controls.floatControl("ambient light intensity", intensity, 0.01f, 0, 100);
        levelMap.setAmbientIntensity(intensity[0]);

        float[] soundVolume = {(float) levelMap.getSoundVolume()};
        Controls.floatControl("sound volume", soundVolume, 0.01f, 0, 1);
        levelMap.setSoundVolume(soundVolume[0]);
    }

    /**
    * Returns a serializable representation of this Level. This can be used to save / restore objects that are no longer needed for serialization.
    * 
    * 
    * @return a serializable representation of this Level or null if there is no serializable representation for this Level or if the Level is
    */
    @Override
    public Serializable getSerializable() {
        return level.getLevelMap();
    }

    /**
    * Synchronizes the state of the cache. This is a no - op if the cache is empty. Note that it is safe to call this method multiple times
    */
    @Override
    public void synchronize() {

    }

    /**
    * Moves the object in the direction of the start vector. This is called by the WorldManger when it is about to move to a new location.
    * 
    * @param transform - The transform associated with this object. Can be null in which case the object is not moved.
    * @param start - The start point of the move. Can be null in which case the object is not moved.
    * @param vector - The direction to move the object in. Can be null
    */
    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {

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
    * Returns whether or not this item can be removed. This is determined by examining the children of this item to see if they are removable.
    * 
    * 
    * @return true if this item can be removed false otherwise ( the default is false ). Note that it is possible to remove items that are in an inconsistent state
    */
    @Override
    public boolean isRemovable() {
        return false;
    }

    /**
    * Returns true if this object can be cloned. The clone is used to determine if a copy of the object is possible without affecting the original and thus making a copy of the object in the same thread.
    * 
    * 
    * @return whether or not this object can be cloned or not ( false by default ). Note that this will return false
    */
    @Override
    public boolean isCloneable() {
        return false;
    }

    /**
    * Creates a copy of this GameObject. This is useful for objects that are shared between different instances of the game object.
    * 
    * 
    * @return A new GameObject that is a copy of this GameObject. Note that the clone will be null
    */
    @Override
    public GameObject clone() {
        return null;
    }

    /**
    * Returns a string representation of this LevelMap. The string representation is used to print information about the level map to the console.
    * 
    * 
    * @return a string representation of this LevelMap ( for debugging purposes only ). The string representation can be parsed by Object#toString ()
    */
    @Override
    public String toString() {
        return "LevelMap";
    }
}
