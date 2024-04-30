package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.level.levelmap.Light;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

import java.util.Locale;

public class LightEditorController extends EditorController {
    private Light light;

    public LightEditorController(final Light light) {
        this.light = light;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

        float[] x = {(float) light.x};
        Controls.floatControl("x", x, 0.1f);
        light.x = x[0];

        float[] y = {(float) light.y};
        Controls.floatControl("y", y, 0.1f);
        light.y = y[0];

        float[] top = {(float) light.top};
        Controls.floatControl("top", top, 0.1f);
        light.top = top[0];

        float[] radiusMin = {(float) light.radiusMin};
        Controls.floatControl("radius min", radiusMin, 0.1f);
        light.radiusMin = radiusMin[0];

        float[] radiusMax = {(float) light.radiusMax};
        Controls.floatControl("radius max", radiusMax, 0.1f);
        light.radiusMax = radiusMax[0];

        float[] color = light.color.toArray();
        Controls.colorPicker("color", color);
        light.color = Color.fromArray(color);
    }

    @Override
    public Serializable getSerializable() {
        return light;
    }

    @Override
    public void synchronize() {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        gameObject.transform.position.set(light.x * renderer.getSprite().getWidth(), light.y * renderer.getSprite().getHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        light.x = (transform.position.x + vector.x) / renderer.getSprite().getWidth();
        light.y = (transform.position.y + vector.y) / renderer.getSprite().getHeight();
    }

    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Spotlight [x=%.3f, y=%.3f]", light.x, light.y);
    }
}
