package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Direction;
import com.webler.untitledgame.level.levelmap.Door;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2d;

import java.util.Arrays;

public class DoorEditorController extends EditorController {
    private Door door;

    public DoorEditorController(EditorComponent editorComponent, Door door) {
        super(editorComponent);
        this.door = door;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());

        int[] x = {door.getX()};
        Controls.intControl("x", x, 0.1f);
        door.setX(x[0]);

        int[] y = {door.getY()};
        Controls.intControl("y", y, 0.1f);
        door.setY(y[0]);

        String[] directions = Arrays.stream(Direction.values()).map(Enum::toString).toArray(String[]::new);
        int selectedDirectionIndex = 0;
        for (int i = 0; i < directions.length; ++i) {
            if(directions[i].equals(door.getDirection().toString())) {
                selectedDirectionIndex = i;
            }
        }
        ImInt selectedDirection = new ImInt(selectedDirectionIndex);
        Controls.comboBox("direction", selectedDirection, directions);
        door.setDirection(Direction.valueOf(directions[selectedDirection.get()]));
    }

    @Override
    public Serializable getSerializable() {
        return door;
    }

    @Override
    public void synchronize() {
        gameObject.transform.position.set(door.getX() * editorComponent.getConfig().gridWidth(), door.getY() * editorComponent.getConfig().gridHeight(), 0);
    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {
        door.setX((int) (Math.floor(0.5 + (transform.position.x + vector.x) / editorComponent.getConfig().gridWidth())));
        door.setY((int) (Math.floor(0.5 + (transform.position.y + vector.y) / editorComponent.getConfig().gridHeight())));
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
        return "Door [x=" + door.getX() + ", y=" + door.getY() + "]";
    }
}
