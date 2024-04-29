package com.webler.untitledgame.editor.windows;

import com.webler.goliath.input.Input;
import com.webler.goliath.math.Rect;
import com.webler.untitledgame.editor.EditorComponent;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class LevelWindow {
    private EditorComponent editor;
    private ImVec2 position;
    private ImVec2 size;
    private Rect levelViewport;

    public LevelWindow(EditorComponent editor) {
        this.editor = editor;
        position = new ImVec2();
        size = new ImVec2();
        levelViewport = new Rect(0, 0, 1, 1);
    }

    public void imgui() {
        ImGui.begin("Level", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImGui.getWindowPos(position);
        ImGui.getWindowSize(size);
        float gameWidth = editor.getEntity().getGame().getFramebuffer().getWidth();
        float gameHeight = editor.getEntity().getGame().getFramebuffer().getHeight();
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
        ImGui.imageButton(editor.getEntity().getGame().getFramebuffer().getTexId(),
                w,
                h,
                0, 1, 1, 0);
        boolean hovered = ImGui.isItemHovered();
        Input.setCaptured(!hovered);
        ImGui.end();
    }

    public Rect getLevelViewport() {
        return levelViewport;
    }
}
