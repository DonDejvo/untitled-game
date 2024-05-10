package com.webler.untitledgame.editor.controllers;

import com.webler.goliath.core.components.Transform;
import com.webler.goliath.graphics.widgets.Controls;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.level.levelmap.Direction;
import com.webler.untitledgame.level.levelmap.Environment;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Serializable;
import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2d;

import java.util.Arrays;

public class LevelMapEditorController extends EditorController {
    private Level level;

    public LevelMapEditorController(EditorComponent editorComponent, Level level) {
        super(editorComponent);
        this.level = level;
    }

    @Override
    public void editorImgui() {
        ImGui.text(this.toString());
        LevelMap levelMap = level.getLevelMap();

        int[] top = {levelMap.ceiling};
        Controls.intControl("Ceiling", top, 0.1f, 1, 100);
        levelMap.ceiling = top[0];

        String[] environments = Arrays.stream(Environment.values()).map(Enum::toString).toArray(String[]::new);
        int selectedEnvironmentIndex = 0;
        for (int i = 0; i < environments.length; ++i) {
            if(environments[i].equals(levelMap.environment.toString())) {
                selectedEnvironmentIndex = i;
            }
        }
        ImInt selectedEnvironment = new ImInt(selectedEnvironmentIndex);
        Controls.comboBox("environment", selectedEnvironment, environments);
        levelMap.environment = Environment.valueOf(environments[selectedEnvironment.get()]);
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
