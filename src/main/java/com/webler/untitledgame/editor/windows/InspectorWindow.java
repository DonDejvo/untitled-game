package com.webler.untitledgame.editor.windows;

import com.webler.goliath.core.GameObject;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EditorController;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;

public class InspectorWindow {
    private final EditorComponent editor;

    public InspectorWindow(EditorComponent editor) {
        this.editor = editor;
    }

    /**
    * ImGui is a method that allows to display the imgui. This method should be called from the event dispatch
    */
    public void imgui() {

        ImGuiIO io = ImGui.getIO();

        ImGui.setNextWindowSize(480, 720, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(io.getDisplaySizeX() - 480 - 20, 60, ImGuiCond.FirstUseEver);

        ImGui.begin("Inspector");
        // This method is called by the editor to display the selected game object.
        if(editor.getSelectedGameObject() != null) {
            EditorController editorController = editor.getSelectedGameObject()
                    .getComponent(EditorController.class, "Controller");
            ImGui.text(editorController.toString());
            editorController.editorImgui();
            // Remove the selected game object from the scene.
            if(editorController.isRemovable()) {
                // Remove the selected game object from the scene.
                if(ImGui.button("Remove")) {
                    editor.getGameObject().getScene().remove(editor.getSelectedGameObject());
                    editor.selectGameObject(null);
                }
            }
            // Clone the editor and select the game object
            if(editorController.isCloneable()) {
                // Clone the game object and select it.
                if(ImGui.button("Clone")) {
                    GameObject clone = editorController.clone();
                    editor.getGameObject().getScene().add(clone);
                    editor.selectGameObject(clone);
                }
            }
        }
        ImGui.end();
    }
}
