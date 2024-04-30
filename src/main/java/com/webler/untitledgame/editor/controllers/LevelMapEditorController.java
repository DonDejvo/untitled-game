package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import org.joml.Vector2d;

public class LevelMapEditorController extends EditorController {
    private Level level;

    public LevelMapEditorController(Level level) {
        this.level = level;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());
        LevelMap levelMap = level.getLevelMap();

        int[] top = {levelMap.ceiling};
        Controls.intControl("Ceiling", top, 0.1f, 1, 100);
        levelMap.ceiling = top[0];
    }

    @Override
    public Serializable getSerializable() {
        return level.getLevelMap();
    }

    @Override
    public void synchronize() {

    }

    @Override
    public void move(Transform transform, Vector2d start, Vector2d vector) {

    }

    @Override
    public void scale(Transform transform, Vector2d start, Vector2d vector) {

    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public String toString() {
        return "LevelMap";
    }
}
