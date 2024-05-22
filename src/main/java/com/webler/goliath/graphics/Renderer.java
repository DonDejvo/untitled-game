package com.webler.goliath.graphics;

import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.utils.AssetPool;
import lombok.Getter;
import org.joml.Matrix4d;
import org.joml.Vector3d;

import static org.lwjgl.opengl.GL20.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Renderer {
    private final List<Mesh> meshes;
    private final List<SpriteBatch> spriteBatches;
    private final List<AmbientLight> ambientLights;
    private final List<SpotLight> spotLights;
    @Getter
    private Fog fog;
    public boolean fogOn = false;
    public boolean lightOn = false;

    public Renderer() {
        meshes = new ArrayList<>();
        spriteBatches = new ArrayList<>();
        ambientLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        fog = new Fog(50, 100, Color.BLACK);
    }

    /**
    * Adds a mesh to the meshes list. This is useful for adding meshes to an already existing mesh or to re - use an existing mesh in a different way
    * 
    * @param mesh - the mesh to be
    */
    public void add(Mesh mesh) {
        meshes.add(mesh);
    }

    /**
    * Removes the mesh from the meshes. This is equivalent to meshes. remove ( mesh ).
    * 
    * @param mesh - the mesh to remove. It must be an instance of Mesh
    * 
    * @return true if the mesh was removed
    */
    public boolean remove(Mesh mesh) {
        return meshes.remove(mesh);
    }

    /**
    * Adds a SpriteRenderer to the batch. If the SpriteRenderer is already in the batch it will be overwritten
    * 
    * @param spriteRenderer - the SpriteRenderer to
    */
    public void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(SpriteBatch batch : spriteBatches) {
            // Adds the sprite renderer to the batch.
            if(!batch.isFull() && batch.getZIndex() == spriteRenderer.getZIndex()) {
                batch.add(spriteRenderer);
                added = true;
            }
        }
        // Adds a SpriteRenderer to the batch.
        if(!added) {
            SpriteBatch batch = createBatch(spriteRenderer.getZIndex());
            batch.add(spriteRenderer);
        }
    }

    /**
    * Removes a SpriteRenderer from the batch. This is useful when you want to remove a sprite that was added with #addSprite ( com. codename1. graphics. SpriteRenderer ) or { @link addSprite ( com. codename1. graphics
    * 
    * @param spriteRenderer
    */
    public boolean remove(SpriteRenderer spriteRenderer) {
        for(SpriteBatch batch : spriteBatches) {
            // Remove the sprite renderer from the batch.
            if(batch.remove(spriteRenderer)) {
                return true;
            }
        }
        return false;
    }

    /**
    * Creates a SpriteBatch and starts it. This is useful for debugging purposes. If you want to run multiple batches at the same time you should use #createBatch ( int ) instead.
    * 
    * @param zIndex - The z - index of the batch.
    * 
    * @return The SpriteBatch that was created and started for debugging purposes. Note that the SpriteBatch will be added to the list of SpriteBatches that this method is called on
    */
    public SpriteBatch createBatch(int zIndex) {
        SpriteBatch spriteBatch = new SpriteBatch(zIndex);
        spriteBatches.add(spriteBatch);
        spriteBatch.start();
        spriteBatches.sort(Comparator.comparingInt(SpriteBatch::getZIndex));
        return spriteBatch;
    }

    /**
    * Adds an ambient light to the list. This does not check if the light is valid or not.
    * 
    * @param ambientLight - The ambient light to add to the
    */
    public void add(AmbientLight ambientLight) {
        ambientLights.add(ambientLight);
    }

    /**
    * Removes the ambient light from the list. Returns true if the ambient light was removed false otherwise
    * 
    * @param ambientLight - the ambient light to remove
    * 
    * @return true if the ambient light was removed false otherwise ( this is for internal use only and should not be used in any circumstances
    */
    public boolean remove(AmbientLight ambientLight) {
        return ambientLights.remove(ambientLight);
    }

    /**
    * Adds a spot light to the list. This does not check if the light is valid or not.
    * 
    * @param spotLight - The spot light to add to the list
    */
    public void add(SpotLight spotLight) {
        spotLights.add(spotLight);
    }

    /**
    * Removes a SpotLight from the list. Returns true if the list changed as a result of the call
    * 
    * @param spotLight - the SpotLight to remove.
    * 
    * @return true if the list changed as a result of the call false otherwise. Note that this does not mean that the light was removed
    */
    public boolean remove(SpotLight spotLight) {
        return spotLights.remove(spotLight);
    }

    /**
    * Renders the scene. This is called by Render#render ( Matrix4d Matrix4d ) and should not be called directly
    * 
    * @param PVMatrix - matrix to use for viewing
    * @param viewMatrix - matrix to use for
    */
    public void render(Matrix4d PVMatrix, Matrix4d viewMatrix) {
        String preFragment = "";
        // Add FOG_ON to the fragment before the fragment.
        if(fogOn) preFragment += "FOG_ON,";
        // Add LIGHT_ON to the fragment.
        if(lightOn) preFragment += "LIGHT_ON,";

        Shader meshShader = AssetPool.getShader("goliath/shaders/mesh.glsl", "", preFragment);
        Shader spriteShader = AssetPool.getShader("goliath/shaders/sprite.glsl", "", preFragment);

        Vector3d[] spotLightVec = new Vector3d[spotLights.size() * 3];
        // This method will create a vector vector of the spot lights.
        for(int i = 0; i < spotLights.size(); ++i) {
            SpotLight spotLight = spotLights.get(i);
            Color lightColor = spotLight.getColor();
            spotLightVec[i * 3] = new Vector3d(spotLight.getGameObject().transform.position);
            spotLightVec[i * 3 + 1] = new Vector3d(lightColor.r, lightColor.g, lightColor.b).mul(spotLight.getIntensity());
            spotLightVec[i * 3 + 2] = new Vector3d(spotLight.getRadiusMin(), spotLight.getRadiusMax(), 0);
        }

        Vector3d ambientColorVec = new Vector3d();
        for (AmbientLight ambientLight : ambientLights) {
            Color ambientColor = ambientLight.getColor();
            ambientColorVec.add(new Vector3d(ambientColor.r, ambientColor.g, ambientColor.b))
                    .mul(ambientLight.getIntensity());
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);

        meshShader.bind();

        // Set the light lights on the mesh shader.
        if(lightOn) {
            meshShader.supplyUniform("u_spot_lights", spotLightVec);
            meshShader.supplyUniform("u_spot_lights_count", spotLights.size());
            meshShader.supplyUniform("u_ambient_color", ambientColorVec);
        }

        // Set the fog on or off shader.
        if(fogOn) {
            Color fogColor = fog.fogColor;

            meshShader.supplyUniform("u_fog_color", new Vector3d(fogColor.r, fogColor.g, fogColor.b));
            meshShader.supplyUniform("u_fog_near", fog.fogNear);
            meshShader.supplyUniform("u_fog_far", fog.fogFar);
        }

        meshShader.supplyUniform("u_view", viewMatrix);

        for(Mesh mesh : meshes) {
            Matrix4d PVM = new Matrix4d(PVMatrix).mul(mesh.getModelMatrix());

            meshShader.supplyUniform("u_PVM", PVM);
            meshShader.supplyUniform("u_model", mesh.getModelMatrix());
            meshShader.supplyUniform("u_color", mesh.getColor());

            mesh.render();
        }

        meshShader.unbind();

        spriteShader.bind();
        spriteShader.supplyUniform("u_PV", PVMatrix);
        spriteShader.supplyUniform("u_view", viewMatrix);

        // Set the fog on or off sprite shader.
        if(fogOn) {
            Color fogColor = fog.fogColor;

            spriteShader.supplyUniform("u_fog_color", new Vector3d(fogColor.r, fogColor.g, fogColor.b));
            spriteShader.supplyUniform("u_fog_near", fog.fogNear);
            spriteShader.supplyUniform("u_fog_far", fog.fogFar);
        }

        for(SpriteBatch spriteBatch : spriteBatches) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            // Enable or disable depth test.
            if(spriteBatch.getZIndex() == -1) {
                glEnable(GL_DEPTH_TEST);
            } else {
                glDisable(GL_DEPTH_TEST);
            }
            spriteBatch.render();
        }

        spriteShader.unbind();
    }

    /**
    * Clears the scene. Clears all meshes sprites and lights and fog settings to default values
    */
    public void clear() {
        for(Mesh mesh : meshes) {
            mesh.destroy();
        }
        meshes.clear();
        for(SpriteBatch batch : spriteBatches) {
            batch.destroy();
        }
        spriteBatches.clear();
        spotLights.clear();
        ambientLights.clear();
        fogOn = false;
        lightOn = false;
        fog.fogColor = Color.BLACK;
        fog.fogNear = 50;
        fog.fogFar = 100;
    }

}
