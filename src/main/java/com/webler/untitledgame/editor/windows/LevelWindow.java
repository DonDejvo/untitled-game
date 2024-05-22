package com.webler.untitledgame.editor.windows;

import com.webler.goliath.input.Input;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.editor.EditorComponent;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import lombok.Getter;

public class LevelWindow {
    private final EditorComponent editor;
    private final ImVec2 position;
    private final ImVec2 size;
    @Getter
    private Rect levelViewport;

    public LevelWindow(EditorComponent editor) {
        this.editor = editor;
        position = new ImVec2();
        size = new ImVec2();
        levelViewport = new Rect(0, 0, 1, 1);
    }

    /**
    * Method to show ImGui in main window. This method is called by editor. onGUI ()
    */
    public void imgui() {

        ImGuiIO io = ImGui.getIO();

        ImGui.setNextWindowSize(800, 600, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(io.getDisplaySizeX() / 2 - 400, 60, ImGuiCond.FirstUseEver);

        ImGui.begin("Level", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.getWindowPos(position);
        ImGui.getWindowSize(size);
        float gameWidth = editor.getGameObject().getGame().getFramebuffer().getWidth();
        float gameHeight = editor.getGameObject().getGame().getFramebuffer().getHeight();
        float x, y, w, h;
//        if(size.x / size.y > gameWidth / gameHeight) {
//            w = size.x;
//            h = size.x * gameHeight / gameWidth;
//        } else {
//            w = size.y * gameWidth / gameHeight;
//            h = size.y;
//        }
        w = gameWidth;
        h = gameHeight;
        x = position.x - Math.max((w - size.x) / 2, 0);
        y = position.y - Math.max((h - size.y) / 2, 0) + 35;
        levelViewport.x = x;
        levelViewport.y = y;
        levelViewport.width = w;
        levelViewport.height = h;
        ImGui.setCursorPos(x - position.x, y - position.y);
        ImGui.imageButton(editor.getGameObject().getGame().getFramebuffer().getTexId(),
                w,
                h,
                0, 1, 1, 0);
        boolean hovered = ImGui.isItemHovered();
        Input.setCaptured(!hovered);
        ImGui.end();
    }

}
