package com.webler.untitledgame.editor.windows;

import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EditorController;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

public class InspectorWindow {
    private EditorComponent editor;

    public InspectorWindow(EditorComponent editor) {
        this.editor = editor;
    }

    public void imgui() {

        ImGuiIO io = ImGui.getIO();

        ImGui.setNextWindowSize(480, 720, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(io.getDisplaySizeX() - 480 - 20, 60, ImGuiCond.FirstUseEver);

        ImGui.begin("Inspector");
        if(editor.getSelectedGameObject() != null) {
            EditorController editorController = editor.getSelectedGameObject()
                    .getComponent(EditorController.class, "Controller");
            editorController.editorImgui();
            if(editorController.isRemovable()) {
                if(ImGui.button("Remove")) {
                    editor.getGameObject().getScene().remove(editor.getSelectedGameObject());
                    editor.selectGameObject(null);
                }
            }
        }
        ImGui.end();
    }
}
