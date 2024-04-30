package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.DebugDraw;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Serializable;
import org.joml.Vector2d;

public abstract class EditorController extends Component {

    public abstract void editorImgui();

    public abstract Serializable getSerializable();

    public abstract void synchronize();

    public abstract void move(Transform transform, Vector2d start, Vector2d vector);

    public abstract void scale(Transform transform, Vector2d start, Vector2d vector);

    public abstract boolean isRemovable();

    @Override
    public void start() {
        synchronize();
    }

    @Override
    public void update(double dt) {
        synchronize();
        if(gameObject.hasComponent("Renderer")) {
            SpriteRenderer spriteRenderer = getComponent(SpriteRenderer.class, "Renderer");
            spriteRenderer.setColor(isSelected() ? Color.ORANGE : Color.WHITE);
            Rect boundingRect = spriteRenderer.getBoundingRect();
            Color color = Color.YELLOW;
            DebugDraw.get().addRect(
                    new Vector2d(boundingRect.x + boundingRect.width / 2, boundingRect.y + boundingRect.height / 2),
                    new Vector2d(boundingRect.width, boundingRect.height), color);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isSelected() {
        GameObject editor = getEntity().getScene().getEntityByName("Editor");
        if (editor != null) {
            return editor.getComponent(EditorComponent.class, "Editor").getSelectedGameObject() == getEntity();
        }
        return false;
    }
}
