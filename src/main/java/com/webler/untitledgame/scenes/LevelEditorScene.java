package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.components.*;
import com.webler.untitledgame.editor.GridLines;
import com.webler.untitledgame.level.Level;
import com.webler.untitledgame.editor.Dockspace;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.EditorConfig;
import com.webler.untitledgame.editor.controllers.LevelMapEditorController;

public class LevelEditorScene extends Scene {
    Dockspace dockspace;

    public LevelEditorScene(Game game) {
        super(game);
    }

    /**
    * Called when the scene is initialized. This is where you can set up your scene by calling setSceneParams
    * 
    * @param params - parameters to initialize the
    */
    @Override
    public void init(SceneParams params) {
        LevelParams levelParams = (LevelParams) params;

        GameObject cameraGameObject = new GameObject(this, "Camera");
        Camera camera = new OrthoCamera(1920, 1080);
        cameraGameObject.addComponent("Camera", camera);
        setCamera(camera);
        add(cameraGameObject);

        dockspace = new Dockspace("Dockspace", getGame());

        EditorConfig editorConfig = new EditorConfig(64, 64);
        GameObject editorGameObject = new GameObject(this, "Editor");
        Level level = new Level();
        EditorComponent editorComponent = new EditorComponent(level, editorConfig, levelParams.getLevelPath());
        editorGameObject.addComponent("Editor", editorComponent);
        editorGameObject.addComponent("GridLines", new GridLines(editorConfig));
        editorGameObject.addComponent("Level", level);
        editorGameObject.addComponent("Controller", new LevelMapEditorController(editorComponent, level));
        add(editorGameObject);
    }



    /**
    * Called when the user presses the imgui button. Opens the image UI and updates the view
    */
    @Override
    public void imgui() {
        dockspace.begin();
        sceneImgui();
        dockspace.end();
    }

    /**
    * Draws the graph. This is called by the DrawingManager to indicate that it is ready to draw
    */
    @Override
    public void draw() {}
}
