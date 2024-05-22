package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.goliath.math.MathUtils;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Platform;
import com.webler.untitledgame.level.levelmap.Serializable;
import com.webler.untitledgame.prefabs.editor.PlatformPrefab;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class PlatformEditorController extends EditorController {
    private final Platform platform;

    public PlatformEditorController(EditorComponent editorComponent, Platform platform) {
        super(editorComponent);
        this.platform = platform;
        this.color = Color.GRAY;
    }

    /**
    * Draws the ImageUI to the screen. This is called from the editor when it is loaded and is used to update the platform
    */
    @Override
    public void editorImgui() {

        int[] x = {platform.getX()};
        Controls.intControl("x", x, 0.1f);
        platform.setX(x[0]);

        int[] y = {platform.getY()};
        Controls.intControl("y", y, 0.1f);
        platform.setY(y[0]);

        int[] width = {platform.getWidth()};
        Controls.intControl("width", width, 0.1f);
        platform.setWidth(width[0]);

        int[] height = {platform.getHeight()};
        Controls.intControl("height", height, 0.1f);
        platform.setHeight(height[0]);

        int[] top = {platform.getTop()};
        Controls.intControl("top", top, 0.1f, 0, 50);
        platform.setTop(top[0]);
        // Sets the ceiling to the top of the screen.
        if(platform.getCeiling() < platform.getTop() + 1) {
            platform.setCeiling(platform.getTop() + 1);
        }

        int[] ceiling = {platform.getCeiling()};
        Controls.intControl("ceiling", ceiling, 0.1f, platform.getTop() + 1, 100);
        platform.setCeiling(ceiling[0]);
    }

    /**
    * Returns the platform that this platform is associated with. This is used to determine whether or not the data should be serialized or unserialized depending on the platform being used.
    * 
    * 
    * @return the platform that this platform is associated with or null if it is not a platform or has no information
    */
    @Override
    public Serializable getSerializable() {
        return platform;
    }

    /**
    * Synchronizes the SpriteRenderer to the Editor's state. This is called after #setPosition ( java. awt. Point )
    */
    @Override
    public void synchronize() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setzIndex(platform.getTop());
        gameObject.transform.position.set(platform.getX() * editorComponent.getConfig().gridWidth(), platform.getY() * editorComponent.getConfig().gridHeight(), 0);
        gameObject.transform.scale.set(platform.getWidth(), platform.getHeight(), 1);
        renderer.getSprite().setRegion(0, 0,
                platform.getWidth() * renderer.getSprite().getTexture().getWidth(),
                platform.getHeight() * renderer.getSprite().getTexture().getHeight());
        Vector3d colorVector = new Vector3d(0.2).lerp(new Vector3d(0.8), Math.pow(MathUtils.clamp(platform.getTop() / 100.0, 0, 1), 0.5));
        this.color = new Color(colorVector.x, colorVector.y, colorVector.z);
    }

    /**
    * Moves the platform to the position specified by vector. This is called by the game logic when it is time to move the platform in a way that does not involve a physics update.
    * 
    * @param transform - The transform of the object that is trying to move.
    * @param start - The position at which the move starts. Used to calculate the platform's x and y coordinates.
    * @param vector - The vector to move the platform to. Use Vector2d#ZERO for zero
    */
    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        platform.setX((int) (Math.floor(0.5 + (transform.position.x + vector.x) / editorComponent.getConfig().gridWidth())));
        platform.setY((int) (Math.floor(0.5 + (transform.position.y + vector.y) / editorComponent.getConfig().gridHeight())));
    }

    /**
    * Scales the component based on the scale and start point. This is called by the UI to scale the component in response to changes in the view's coordinate system.
    * 
    * @param transform - The transform of the component. Not used.
    * @param start - The starting point of the scale. Not used.
    * @param vector - The vector in which to scale the component. Not used
    */
    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {
        platform.setWidth(Math.max((int) (transform.scale.x + Math.floor(0.5 + vector.x / editorComponent.getConfig().gridWidth())), 1));
        platform.setHeight(Math.max((int) (transform.scale.y + Math.floor(0.5 + vector.y / editorComponent.getConfig().gridHeight())), 1));
    }

    /**
    * Creates a clone of this prefab. It is the responsibility of the caller to ensure that the clone is valid before calling this method.
    * 
    * 
    * @return a clone of this prefab with the same state as it was before this method was called. Note that this prefab does not have to be associated with an editor
    */
    @Override
    public GameObject clone() {
        return new PlatformPrefab(editorComponent, new Platform(platform.getX(), platform.getY(), platform.getWidth(), platform.getHeight(), platform.getTop(), platform.getCeiling())).create(gameObject.getScene());
    }

    /**
    * Returns a string representation of this Platform. The string representation is suitable for debugging purposes and is not intended to be used directly from your code.
    * 
    * 
    * @return a string representation of this Platform in the form x = y where x and y are the coordinates of the
    */
    @Override
    public String toString() {
        return "Platform [x=" + platform.getX() + ", y=" + platform.getY() + "]";
    }
}
