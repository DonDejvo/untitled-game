package com.webler.untitledgame.editor.windows;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.controllers.EditorController;
import com.webler.untitledgame.level.levelmap.*;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;

import java.nio.file.Path;
import java.util.List;

public class HierarchyWindow {
    private final EditorComponent editor;
    private int selectedIdx;

    public HierarchyWindow(EditorComponent editor) {
        this.editor = editor;
        selectedIdx = -1;
    }

    /**
    * This method displays the ImGui Hierarchy. It is called when the user clicks on the Hierarchy
    */
    public void imgui() {
        Scene scene = editor.getGameObject().getScene();
        String levelName = editor.getCurrentPath() == null ?
                "<Unsaved>" : Path.of(editor.getCurrentPath()).getFileName().toString();
        int i = 0;

        ImGui.setNextWindowSize(480, 720, ImGuiCond.FirstUseEver);
        ImGui.setNextWindowPos(20, 60, ImGuiCond.FirstUseEver);

        ImGui.begin("Hierarchy");

        // This method is called by the main method to display the tree.
        if(ImGui.treeNode(levelName)) {
            imguiLevelObjectNode(scene.getEntityByName("Editor"), i++);
            List<GameObject> platformGameObjects = scene.getEntitiesByTag(Platform.TAG);
            // This method is called by the main method to display the game objects in the scene.
            if(ImGui.treeNode("Platforms (" + platformGameObjects.size() + ")")) {
                for(GameObject platformGameObject : platformGameObjects) {
                    imguiLevelObjectNode(platformGameObject, i++);
                }
                ImGui.treePop();
            }
            List<GameObject> lightGameObjects = scene.getEntitiesByTag(Light.TAG);
            // This method is called by the main loop to display the light objects.
            if(ImGui.treeNode("Lights (" + lightGameObjects.size() + ")")) {
                for(GameObject lightGameObject : lightGameObjects) {
                    imguiLevelObjectNode(lightGameObject, i++);
                }
                ImGui.treePop();
            }
            List<GameObject> entityGameObjects = scene.getEntitiesByTag(Entity.TAG);
            // This method is called by the main method to display the game objects in the tree.
            if(ImGui.treeNode("Entities (" + entityGameObjects.size() + ")")) {
                for(GameObject entityGameObject : entityGameObjects) {
                    imguiLevelObjectNode(entityGameObject, i++);
                }
                ImGui.treePop();
            }
            List<GameObject> doorGameObjects = scene.getEntitiesByTag(Door.TAG);
            // This method is called by the DoorManager to display the doors.
            if(ImGui.treeNode("Doors (" + doorGameObjects.size() + ")")) {
                for(GameObject doorGameObject : doorGameObjects) {
                    imguiLevelObjectNode(doorGameObject, i++);
                }
                ImGui.treePop();
            }
            ImGui.treePop();
        }
        ImGui.end();
    }

    /**
    * Creates and displays a tree node to control the level object. This is used for the ImGui menu
    * 
    * @param gameObject - The object to control.
    * @param index - The index of the node to be created and
    */
    private void imguiLevelObjectNode(GameObject gameObject, int index) {
        EditorController controller = gameObject.getComponent(EditorController.class, "Controller");
        int nodeFlags = ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
        // Set the selected node to the selected node.
        if(selectedIdx == index) {
            nodeFlags |= ImGuiTreeNodeFlags.Selected;
        }
        ImGui.treeNodeEx(controller.toString(), nodeFlags);
        // Selects the item in the list.
        if(ImGui.isItemClicked()) {
            selectedIdx = index;
            editor.selectGameObject(gameObject);
        }
    }
}
