package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Entity;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

import java.util.Locale;

public class EntityEditorController extends EditorController {
    private Entity entity;

    public EntityEditorController(EditorComponent editorComponent, Entity entity) {
        super(editorComponent);
        this.entity = entity;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

        float[] x = {(float) entity.getX()};
        Controls.floatControl("x", x, 0.1f);
        entity.setX(x[0]);

        float[] y = {(float) entity.getY()};
        Controls.floatControl("y", y, 0.1f);
        entity.setY(y[0]);
    }

    @Override
    public Serializable getSerializable() {
        return entity;
    }

    @Override
    public void synchronize() {
        gameObject.transform.position.set(entity.getX() * editorComponent.getConfig().gridWidth(), entity.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        entity.setX((transform.position.x + vector.x) / editorComponent.getConfig().gridWidth());
        entity.setY((transform.position.y + vector.y) / editorComponent.getConfig().gridHeight());
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
        return String.format(Locale.US, "%s [x=%.3f, y=%.3f]", entity.getName(), entity.getX(), entity.getY());
    }
}
