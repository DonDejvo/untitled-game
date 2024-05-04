package com.webler.goliath.utils;

import com.webler.goliath.graphics.Shader;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.font.BitmapFont;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
            throw new RuntimeException("Could not load resource path '" + resourceName + "'");
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
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
            if(is == null) {
                throw new RuntimeException("Could not load resource path '" + resourceName + "'");
            }
            String textSource = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Shader shader = Shader.loadFromTextSource(textSource, preVertex, preFragment);
            shader.linkShader();
            shaders.put(key, shader);
            return shader;
        } catch (IOException e) {
            throw new RuntimeException("Could not load resource path '" + resourceName + "'");
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
        throw new RuntimeException("Could not load resource path '" + resourceName + "'");
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
        throw new RuntimeException("Could not load resource path '" + resourceName + "'");
    }

    public static void destroy() {
        shaders.forEach(((s, shader) -> shader.destroy()));
        textures.forEach((s, texture) -> texture.destroy());
    }
}
