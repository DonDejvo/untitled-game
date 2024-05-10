package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.components.*;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.GridLines;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.editor.Dockspace;
import com.webler.untitledgame.editor.EditorComponent;
import com.webler.untitledgame.editor.EditorConfig;
import com.webler.untitledgame.editor.controllers.LevelMapEditorController;
import imgui.ImGui;

public class LevelEditorScene extends Scene {
    Dockspace dockspace;

    public LevelEditorScene(Game game) {
        super(game);
    }

    @Override
    public void init(SceneParams params) {
        LevelParams levelParams = (LevelParams) params;

        Texture tilesTexture = AssetPool.getTexture("untitled-game/spritesheets/tileset.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/tileset.png",
                new Spritesheet(tilesTexture, 16, 16, 70, 9, 2, 2));

        Texture catgirlsTexture = AssetPool.getTexture("untitled-game/spritesheets/catgirls.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/catgirls.png",
                new Spritesheet(catgirlsTexture, 66, 86, 107, 12, 0, 0));

        Texture houseTexture = AssetPool.getTexture("untitled-game/spritesheets/house_asset.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/house_asset.png",
                new Spritesheet(houseTexture, 48, 48, 31, 6, 16, 16));

        Texture ghostTexture = AssetPool.getTexture("untitled-game/spritesheets/ghost.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/ghost.png",
                new Spritesheet(ghostTexture, 16, 16, 3, 3, 0, 0));

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



    @Override
    public void imgui() {
        dockspace.begin();
        sceneImgui();
        dockspace.end();
    }

    @Override
    public void draw() {}
}
