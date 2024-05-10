package com.webler.untitledgame.editor;

import com.webler.goliath.Game;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGuiDockNode;
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

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);

        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin(title, new ImBoolean(true), windowFlags);
        ImGui.popStyleVar();
        ImGui.popStyleVar(2);

        int dockSpaceId = ImGui.getID(title);
        ImGui.dockSpace(dockSpaceId);
    }

    public void end() {
        ImGui.end();
    }
}
