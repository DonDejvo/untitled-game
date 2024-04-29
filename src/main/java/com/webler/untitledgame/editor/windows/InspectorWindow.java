package com.webler.untitledgame.editor.windows;

import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EditorController;
import imgui.ImGui;
import imgui.flag.ImGuiCond;

public class InspectorWindow {
    private EditorComponent editor;

    public InspectorWindow(EditorComponent editor) {
        this.editor = editor;
    }

    public void imgui() {
        ImGui.setNextWindowSize(300, 150, ImGuiCond.FirstUseEver);

        ImGui.begin("Inspector");
        if(editor.getSelectedGameObject() != null) {
            EditorController editorController = editor.getSelectedGameObject()
                    .getComponent(EditorController.class, "Controller");
            editorController.editorImgui();
            if(editorController.isRemovable()) {
                if(ImGui.button("Remove")) {
                    editor.getEntity().getScene().remove(editor.getSelectedGameObject());
                    editor.selectGameObject(null);
                }
            }
        }
        ImGui.end();
    }
}
