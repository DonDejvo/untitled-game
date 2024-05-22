package com.webler.untitledgame;

import com.webler.goliath.Config;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.utils.AssetPool;

public class MyConfig extends Config {

    public MyConfig(String startScene, SceneParams startSceneParams, boolean loggerEnabled) {
        super("Untitled Game", 1920, 1080, startScene, startSceneParams, loggerEnabled);
    }

    /**
    * Loads spritesheets for this game into the AssetPool. This is called by Unity's preloader
    */
    @Override
    public void preload() {
        Texture tilesTexture = AssetPool.getTexture("untitled-game/spritesheets/tileset.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/tileset.png",
                new Spritesheet(tilesTexture, 16, 16, 70, 9, 2, 2));

        Texture ghostTexture = AssetPool.getTexture("untitled-game/spritesheets/ghost.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/ghost.png",
                new Spritesheet(ghostTexture, 16, 16, 3, 3, 0, 0));

        Texture playerTexture = AssetPool.getTexture("untitled-game/spritesheets/player.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/player.png",
                new Spritesheet(playerTexture, 16, 16, 10, 5, 0, 0));

        Texture knightTexture = AssetPool.getTexture("untitled-game/spritesheets/knight.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/knight.png",
                new Spritesheet(knightTexture, 16, 16, 12, 6, 0, 0));

        Texture goblinTexture = AssetPool.getTexture("untitled-game/spritesheets/goblin.png");
        AssetPool.addSpritesheet("untitled-game/spritesheets/goblin.png",
                new Spritesheet(goblinTexture, 16, 16, 12, 6, 0, 0));
    }
}
