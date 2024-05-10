package com.webler.goliath.utils;

import com.webler.goliath.animation.Animation;
import com.webler.goliath.animation.Frame;
import com.webler.goliath.exceptions.ResourceFormatException;
import com.webler.goliath.exceptions.ResourceNotFoundException;
import com.webler.goliath.graphics.Shader;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.font.BitmapFont;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static final Map<String, BitmapFont> bitmapFonts = new HashMap<>();
    private static final Map<String, Animation> animations = new HashMap<>();

    public static AssetPool get() {
        return new AssetPool();
    }

    public static Texture getTexture(String resourceName) {
        if(textures.containsKey(resourceName)) {
            return textures.get(resourceName);
        }
        try {
            URL url = ClassLoader.getSystemResource(resourceName);
            Texture texture = new Texture();
            texture.loadFromImage(ImageIO.read(url));
            textures.put(resourceName, texture);
            return texture;
        } catch (IOException e) {
            throw new ResourceNotFoundException("Could not load resource path '" + resourceName + "'");
        }
    }

    public static Shader getShader(String resourceName) {
        return getShader(resourceName, "", "");
    }

    public static Shader getShader(String resourceName, String preVertex, String preFragment) {
        String key = resourceName + "$" + preVertex + ":" + preFragment;
        if(shaders.containsKey(key)) {
            return shaders.get(key);
        }
        Shader shader = Shader.load(resourceName, preVertex, preFragment);
        shader.linkShader();
        shaders.put(key, shader);
        return shader;
    }

    public static Animation getAnimation(String resourceName) {
        if(animations.containsKey(resourceName)) {
            return animations.get(resourceName);
        }
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new ResourceNotFoundException(resourceName);
        }
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            line = reader.readLine().trim();
            String[] tokens = line.split(" ");
            int frameCount = Integer.parseInt((tokens[2]));
            int frameWidth = Integer.parseInt((tokens[0]));
            int frameHeight = Integer.parseInt((tokens[1]));
            Frame[] frames = new Frame[frameCount];
            for(int i = 0; i < frameCount; ++i) {
                line = reader.readLine().trim();
                tokens = line.split(" ");
                int frameX = Integer.parseInt(tokens[0]);
                int frameY = Integer.parseInt(tokens[1]);
                int frameDuration = Integer.parseInt(tokens[2]);
                frames[i] = new Frame(frameX, frameY, frameDuration * 0.001);
            }
            File file = new File(resourceName);
            Animation animation = new Animation(file.getName(), frameWidth, frameHeight, frames);
            animations.put(resourceName, animation);
            return animation;
        } catch (Exception e) {
            throw new ResourceFormatException(resourceName + e.getMessage());
        }
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        if(!spritesheets.containsKey(resourceName)) {
            spritesheets.put(resourceName, spritesheet);
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        if(spritesheets.containsKey(resourceName)) {
            return spritesheets.get(resourceName);
        }
        throw new ResourceNotFoundException("Could not load resource path '" + resourceName + "'");
    }

    public static void addBitmapFont(String resourceName, BitmapFont bitmapFont) {
        if(!bitmapFonts.containsKey(resourceName)) {
            bitmapFonts.put(resourceName, bitmapFont);
        }
    }

    public static BitmapFont getBitmapFont(String resourceName) {
        if(bitmapFonts.containsKey(resourceName)) {
            return bitmapFonts.get(resourceName);
        }
        throw new ResourceNotFoundException("Could not load resource path '" + resourceName + "'");
    }

    public static void destroy() {
        shaders.forEach(((s, shader) -> shader.destroy()));
        textures.forEach((s, texture) -> texture.destroy());
    }
}
