package com.webler.untitledgame;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.webler.goliath.Config;
import com.webler.goliath.Game;
import com.webler.untitledgame.scenes.LevelEditorScene;
import com.webler.untitledgame.scenes.LevelScene;
import com.webler.untitledgame.scenes.LevelParams;

public class Main {

    /**
    * Entry point for the LevelEditorScene. This is the main method that will be called when the program starts
    * 
    * @param args - command line arguments to
    */
    public static void main(String[] args) {
        boolean enableLogging = true;
        boolean showDemo = false;

        for (String arg : args) {
            // If the command line arguments are not specified in the command line arguments disable logging show demo level show demo level
            if (arg.equals("--disable-logging")) {
                enableLogging = false;
                break;
            // Show demo level if the command line argument is show demo level
            } else if (arg.equals("--show-demo-level")) {
                showDemo = true;
            }
        }

        String demoLevelPath = null;
        URL url = ClassLoader.getSystemResource("untitled-game/demo_level.xml");
        try {
            File file = new File(url.toURI());
            demoLevelPath = file.getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
        }

        Config config = new MyConfig(
                "LevelEditorScene",
                new LevelParams(showDemo ? demoLevelPath : null),
                enableLogging
        );
        Game game = new Game(config);
        game.registerScene(LevelScene.class);
        game.registerScene(LevelEditorScene.class);
        game.run();
    }
}