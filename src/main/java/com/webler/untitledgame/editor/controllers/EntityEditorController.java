package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.level.levelmap.Entity;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2d;

import java.util.Locale;

public class EntityEditorController extends EditorController {
    private Entity entity;

    public EntityEditorController(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

        ImString name = new ImString(entity.name, 256);
        Controls.textInput("name", name);
        entity.name = name.get();


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
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        gameObject.transform.position.set(entity.x * renderer.getSprite().getWidth(), entity.y * renderer.getSprite().getHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        SpriteRenderer renderer = getComponent(SpriteRenderer.class, "Renderer");
        entity.x = (transform.position.x + vector.x) / renderer.getSprite().getWidth();
        entity.y = (transform.position.y + vector.y) / renderer.getSprite().getHeight();
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
