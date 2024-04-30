package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.level.levelmap.Platform;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

public class PlatformEditorController extends EditorController {
    private Platform platform;

    public PlatformEditorController(final Platform platform) {
        this.platform = platform;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

        int[] x = {platform.x};
        Controls.intControl("x", x, 0.1f);
        platform.x = x[0];

        int[] y = {platform.y};
        Controls.intControl("y", y, 0.1f);
        platform.y = y[0];

        int[] width = {platform.width};
        Controls.intControl("width", width, 0.1f);
        platform.width = width[0];

        int[] height = {platform.height};
        Controls.intControl("height", height, 0.1f);
        platform.height = height[0];

        int[] top = {platform.top};
        Controls.intControl("top", top, 0.1f, 0, 50);
        platform.top = top[0];

        int[] ceiling = {platform.ceiling};
        Controls.intControl("ceiling", ceiling, 0.1f, platform.top + 1, 100);
        platform.ceiling = ceiling[0];
    }

    @Override
    public Serializable getSerializable() {
        return platform;
    }

    @Override
    public void synchronize() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        renderer.setzIndex(platform.top);
        gameObject.transform.position.set(platform.x * renderer.getSprite().getWidth(), platform.y * renderer.getSprite().getHeight(), 0);
        gameObject.transform.scale.set(platform.width, platform.height, 1);
        renderer.getSprite().setRegion(0, 0,
                platform.width * renderer.getSprite().getTexture().getWidth(),
                platform.height * renderer.getSprite().getTexture().getHeight());
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        platform.x = (int)(Math.floor(0.5 + (transform.position.x + vector.x) / renderer.getSprite().getWidth()));
        platform.y = (int)(Math.floor(0.5 + (transform.position.y + vector.y) / renderer.getSprite().getHeight()));
    }

    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        platform.width = Math.max((int)(transform.scale.x + Math.floor(0.5 + vector.x / renderer.getSprite().getWidth())), 1);
        platform.height = Math.max((int)(transform.scale.y + Math.floor(0.5 + vector.y / renderer.getSprite().getHeight())), 1);
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public String toString() {
        return "Platform [x=" + platform.x + ", y=" + platform.y + "]";
    }
}
