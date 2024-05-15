package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Platform;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

public class PlatformEditorController extends EditorController {
    private Platform platform;

    public PlatformEditorController(EditorComponent editorComponent, Platform platform) {
        super(editorComponent);
        this.platform = platform;
        this.color = Color.GRAY;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

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
        if(platform.getCeiling() < platform.getTop() + 1) {
            platform.setCeiling(platform.getTop() + 1);
        }

        int[] ceiling = {platform.getCeiling()};
        Controls.intControl("ceiling", ceiling, 0.1f, platform.getTop() + 1, 100);
        platform.setCeiling(ceiling[0]);
    }

    @Override
    public Serializable getSerializable() {
        return platform;
    }

    @Override
    public void synchronize() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setzIndex(platform.getTop());
        gameObject.transform.position.set(platform.getX() * editorComponent.getConfig().gridWidth(), platform.getY() * editorComponent.getConfig().gridHeight(), 0);
        gameObject.transform.scale.set(platform.getWidth(), platform.getHeight(), 1);
        renderer.getSprite().setRegion(0, 0,
                platform.getWidth() * renderer.getSprite().getTexture().getWidth(),
                platform.getHeight() * renderer.getSprite().getTexture().getHeight());
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        platform.setX((int) (Math.floor(0.5 + (transform.position.x + vector.x) / editorComponent.getConfig().gridWidth())));
        platform.setY((int) (Math.floor(0.5 + (transform.position.y + vector.y) / editorComponent.getConfig().gridHeight())));
    }

    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {
        platform.setWidth(Math.max((int) (transform.scale.x + Math.floor(0.5 + vector.x / editorComponent.getConfig().gridWidth())), 1));
        platform.setHeight(Math.max((int) (transform.scale.y + Math.floor(0.5 + vector.y / editorComponent.getConfig().gridHeight())), 1));
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public String toString() {
        return "Platform [x=" + platform.getX() + ", y=" + platform.getY() + "]";
    }
}
