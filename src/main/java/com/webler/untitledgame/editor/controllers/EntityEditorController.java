package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Entity;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import imgui.type.ImString;
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

        float[] x = {(float) entity.x};
        Controls.floatControl("x", x, 0.1f);
        entity.x = x[0];

        float[] y = {(float) entity.y};
        Controls.floatControl("y", y, 0.1f);
        entity.y = y[0];
    }

    @Override
    public Serializable getSerializable() {
        return entity;
    }

    @Override
    public void synchronize() {
        gameObject.transform.position.set(entity.x * editorComponent.getConfig().gridWidth(), entity.y * editorComponent.getConfig().gridHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        entity.x = (transform.position.x + vector.x) / editorComponent.getConfig().gridWidth();
        entity.y = (transform.position.y + vector.y) / editorComponent.getConfig().gridHeight();
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
        return String.format(Locale.US, "%s [x=%.3f, y=%.3f]", entity.name, entity.x, entity.y);
    }
}
