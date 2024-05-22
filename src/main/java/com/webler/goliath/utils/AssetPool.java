package com.webler.goliath.utils;

import com.webler.goliath.animation.Animation;
import com.webler.goliath.animation.Frame;
import com.webler.goliath.audio.Sound;
import com.webler.goliath.exceptions.ResourceFormatException;
import com.webler.goliath.exceptions.ResourceNotFoundException;
import com.webler.goliath.graphics.Shader;
import com.webler.goliath.graphics.Spritesheet;
import com.webler.goliath.graphics.Texture;
import com.webler.goliath.graphics.font.BitmapFont;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {
    private static final Map<String, Texture> textures = new HashMap<>();
    private static final Map<String, Shader> shaders = new HashMap<>();
    private static final Map<String, Sound> sounds = new HashMap<>();
    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();
    private static final Map<String, BitmapFont> bitmapFonts = new HashMap<>();
    private static final Map<String, Animation> animations = new HashMap<>();

    /**
    * Loads a texture from a resource. The resource must be a URL and it must have a. jpg extension
    * 
    * @param resourceName - the name of the texture
    * 
    * @return the texture or null if not found or could not be loaded ( for example if it's not an image
    */
    public static Texture getTexture(String resourceName) {
        // Returns the textures for the given resource name.
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
            e.printStackTrace(System.err);
            throw new ResourceNotFoundException(resourceName);
        }
    }

    /**
    * Returns a Shader by name. Note that this is a shortcut for #getShader ( String String ) with the " v " flag set to " 0 ".
    * 
    * @param resourceName - the name of the shader to retrieve.
    * 
    * @return the shader or null if not found or error. The caller must dispose this object when finished with it
    */
    public static Shader getShader(String resourceName) {
        return getShader(resourceName, "", "");
    }

    /**
    * Loads and links a Shader. This is useful for debugging and to avoid loading shaders that are already loaded in the application
    * 
    * @param resourceName - the name of the shader to load
    * @param preVertex - the vertex buffer to pre - link the shader with
    * @param preFragment - the fragment buffer to pre - link the shader with
    * 
    * @return the Shader or null if not found or could not be loaded and link was not made for some
    */
    public static Shader getShader(String resourceName, String preVertex, String preFragment) {
        String key = resourceName + "$" + preVertex + ":" + preFragment;
        // Returns the shaders for the given key.
        if(shaders.containsKey(key)) {
            return shaders.get(key);
        }
        Shader shader = Shader.load(resourceName, preVertex, preFragment);
        shader.linkShader();
        shaders.put(key, shader);
        return shader;
    }

    /**
    * Loads a sound from the given resource name. If the sound is not loaded it will be loaded and cached for future use
    * 
    * @param resourceName - the name of the sound
    * 
    * @return the sound or null if it could not be
    */
    public static Sound getSound(String resourceName) {
        // Get the sound object for the given resource name.
        if(sounds.containsKey(resourceName)) {
            return sounds.get(resourceName);
        }
        Sound sound = new Sound();
        sound.load(resourceName);
        sounds.put(resourceName, sound);
        return sound;
    }

    /**
    * Loads an animation from a resource. The resource must be in the format returned by #getResource ( String )
    * 
    * @param resourceName - the name of the resource to load
    * 
    * @return the animation or null if not found or could not be loaded ( in which case it will be thrown
    */
    public static Animation getAnimation(String resourceName) {
        // Returns the animation for the given resource name.
        if(animations.containsKey(resourceName)) {
            return animations.get(resourceName);
        }
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(resourceName);
        // Throws an exception if the input stream is null.
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
            // Reads a frame from the reader.
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
            e.printStackTrace(System.err);
            throw new ResourceFormatException(resourceName, "Invalid resource format");
        }
    }

    /**
    * Adds Spritesheet to Map of Spritesheets. If Map already contains Spritesheet with same name nothing happens
    * 
    * @param resourceName - Name of the resource to be used
    * @param spritesheet - Spritesheet to be added to
    */
    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        // Add a Spritesheet to the list of spritesheets.
        if(!spritesheets.containsKey(resourceName)) {
            spritesheets.put(resourceName, spritesheet);
        }
    }

    /**
    * Gets Spritesheet by name. Throws ResourceNotFoundException if not found. This is useful for debugging purposes.
    * 
    * @param resourceName - Name of the Spritesheet to retrieve.
    * 
    * @return The Spritesheet with the given name or null if not found ( in which case RuntimeException is thrown
    */
    public static Spritesheet getSpritesheet(String resourceName) {
        // Returns the sprite sheet with the given resource name.
        if(spritesheets.containsKey(resourceName)) {
            return spritesheets.get(resourceName);
        }
        throw new ResourceNotFoundException(resourceName);
    }

    /**
    * Adds a BitmapFont to the list of bitmap fonts. This is useful for customizing fonts that are loaded on the fly.
    * 
    * @param resourceName - The resource name of the font to add.
    * @param bitmapFont - The font to add to the list
    */
    public static void addBitmapFont(String resourceName, BitmapFont bitmapFont) {
        // Adds a bitmap font to the bitmaps.
        if(!bitmapFonts.containsKey(resourceName)) {
            bitmapFonts.put(resourceName, bitmapFont);
        }
    }

    /**
    * Returns the BitmapFont with the given name. If there is no such font a ResourceNotFoundException is thrown.
    * 
    * @param resourceName - Name of the resource to retrieve. Must be a valid resource name
    * 
    * @return The BitmapFont with the given
    */
    public static BitmapFont getBitmapFont(String resourceName) {
        // Returns the bitmap font with the given resource name.
        if(bitmapFonts.containsKey(resourceName)) {
            return bitmapFonts.get(resourceName);
        }
        throw new ResourceNotFoundException(resourceName);
    }

    /**
    * Destroys all shaders textures and sounds. This is useful when you want to clean up your game
    */
    public static void destroy() {
        shaders.forEach(((s, shader) -> shader.destroy()));
        textures.forEach((s, texture) -> texture.destroy());
        sounds.forEach((s, sound) -> sound.destroy());
    }
}
