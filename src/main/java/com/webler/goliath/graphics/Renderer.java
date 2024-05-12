package com.webler.goliath.graphics;

import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.utils.AssetPool;
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

    public void add(Mesh mesh) {
        meshes.add(mesh);
    }

    public boolean remove(Mesh mesh) {
        return meshes.remove(mesh);
    }

    public void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(SpriteBatch batch : spriteBatches) {
            if(!batch.isFull() && batch.getzIndex() == spriteRenderer.getzIndex()) {
                batch.add(spriteRenderer);
                added = true;
            }
        }
        if(!added) {
            SpriteBatch batch = createBatch(spriteRenderer.getzIndex());
            batch.add(spriteRenderer);
        }
    }

    public boolean remove(SpriteRenderer spriteRenderer) {
        for(SpriteBatch batch : spriteBatches) {
            if(batch.remove(spriteRenderer)) {
                return true;
            }
        }
        return false;
    }

    public SpriteBatch createBatch(int zIndex) {
        SpriteBatch spriteBatch = new SpriteBatch(zIndex);
        spriteBatches.add(spriteBatch);
        spriteBatch.start();
        spriteBatches.sort(Comparator.comparingInt(SpriteBatch::getzIndex));
        return spriteBatch;
    }

    public void add(AmbientLight ambientLight) {
        ambientLights.add(ambientLight);
    }

    public boolean remove(AmbientLight ambientLight) {
        return ambientLights.remove(ambientLight);
    }

    public void add(SpotLight spotLight) {
        spotLights.add(spotLight);
    }

    public boolean remove(SpotLight spotLight) {
        return spotLights.remove(spotLight);
    }

    public void render(Matrix4d PVMatrix, Matrix4d viewMatrix) {
        String preFragment = "";
        if(fogOn) preFragment += "FOG_ON,";
        if(lightOn) preFragment += "LIGHT_ON,";

        Shader meshShader = AssetPool.getShader("goliath/shaders/mesh.glsl", "", preFragment);
        Shader spriteShader = AssetPool.getShader("goliath/shaders/sprite.glsl", "", preFragment);

        Vector3d[] spotLightVec = new Vector3d[spotLights.size() * 3];
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

        if(lightOn) {
            meshShader.supplyUniform("u_spot_lights", spotLightVec);
            meshShader.supplyUniform("u_spot_lights_count", spotLights.size());
            meshShader.supplyUniform("u_ambient_color", ambientColorVec);
        }

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

        if(fogOn) {
            Color fogColor = fog.fogColor;

            spriteShader.supplyUniform("u_fog_color", new Vector3d(fogColor.r, fogColor.g, fogColor.b));
            spriteShader.supplyUniform("u_fog_near", fog.fogNear);
            spriteShader.supplyUniform("u_fog_far", fog.fogFar);
        }

        for(SpriteBatch spriteBatch : spriteBatches) {
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            if(spriteBatch.getzIndex() == -1) {
                glEnable(GL_DEPTH_TEST);
            } else {
                glDisable(GL_DEPTH_TEST);
            }
            spriteBatch.render();
        }

        spriteShader.unbind();
    }

    public void destroy() {
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

    public Fog getFog() {
        return fog;
    }
}
