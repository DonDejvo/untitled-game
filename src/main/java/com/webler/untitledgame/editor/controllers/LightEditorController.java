package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Light;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

import java.util.Locale;

public class LightEditorController extends EditorController {
    private Light light;

    public LightEditorController(EditorComponent editorComponent, Light light) {
        super(editorComponent);
        this.light = light;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

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
        Controls.floatControl("intensity", intensity, 0.1f);
        light.setIntensity(intensity[0]);
    }

    @Override
    public Serializable getSerializable() {
        return light;
    }

    @Override
    public void synchronize() {
        gameObject.transform.position.set(light.getX() * editorComponent.getConfig().gridWidth(), light.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        light.setX((transform.position.x + vector.x) / editorComponent.getConfig().gridWidth());
        light.setY((transform.position.y + vector.y) / editorComponent.getConfig().gridHeight());
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
        return String.format(Locale.US, "Spotlight [x=%.3f, y=%.3f]", light.getX(), light.getY());
    }
}
