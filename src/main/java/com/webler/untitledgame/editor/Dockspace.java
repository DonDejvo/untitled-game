package com.webler.untitledgame.editor;

import com.webler.goliath.Game;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

public class Dockspace {
    private Game game;
    private String title;

    public Dockspace(String title, Game game) {
        this.game = game;
        this.title = title;
    }

    public void begin() {
        int windowFlags = ImGuiWindowFlags.MenuBar;

        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(game.getWidth(), game.getHeight());

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin(title, new ImBoolean(true), windowFlags);
        ImGui.dockSpace(ImGui.getID(title));
    }

    public void end() {
        ImGui.end();
    }
}
