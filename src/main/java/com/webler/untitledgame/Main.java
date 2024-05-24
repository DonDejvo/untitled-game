package com.webler.untitledgame;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        Path demoLevelPath = Paths.get(System.getProperty("user.home"), "untitled-game", "demo_level.xml");
        boolean success = saveDemoLevel(demoLevelPath);

        Config config = new MyConfig(
                "LevelEditorScene",
                new LevelParams((showDemo && success) ? demoLevelPath.toString() : null),
                enableLogging
        );
        Game game = new Game(config);
        game.registerScene(LevelScene.class);
        game.registerScene(LevelEditorScene.class);
        game.run();
    }

    private static boolean saveDemoLevel(Path path) {
        InputStream is = ClassLoader.getSystemResourceAsStream("untitled-game/levels/demo_level.xml");
        if(is == null) {
            return false;
        }
        try {
            if(Files.isDirectory(path)) {
                return false;
            }
            if(Files.exists(path)) {
                return true;
            }

            Path parentDirectory = path.getParent();
            if(!Files.isDirectory(parentDirectory)) {
                Files.createDirectories(parentDirectory);
            }

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Files.writeString(path, content);

        } catch (IOException e) {
            return false;
        }
        return true;
    }
}