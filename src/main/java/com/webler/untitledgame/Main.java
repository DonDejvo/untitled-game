package com.webler.untitledgame;

import com.webler.goliath.Config;
import com.webler.goliath.Game;
import com.webler.untitledgame.scenes.LevelEditorScene;
import com.webler.untitledgame.scenes.LevelScene;
import com.webler.untitledgame.scenes.LevelParams;
import com.webler.untitledgame.scenes.TestScene;

public class Main {

    public static void main(String[] args) {

        Config config = new Config(
                "Untitled Game",
                1920,
                1080,
                "LevelEditorScene",
                new LevelParams(null)
        );
        Game game = new Game(config);
        game.registerScene(LevelScene.class);
        game.registerScene(LevelEditorScene.class);
        game.registerScene(TestScene.class);
        game.run();
    }
}