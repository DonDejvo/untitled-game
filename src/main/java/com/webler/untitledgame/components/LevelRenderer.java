package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Mesh;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.Fog;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.geometry.LevelGeometry;
import com.webler.untitledgame.level.levelmap.Entity;
import com.webler.untitledgame.level.levelmap.LevelMap;
import com.webler.untitledgame.level.levelmap.Light;
import com.webler.untitledgame.scenes.TestParams;
import org.joml.Vector3d;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;

public class LevelRenderer extends Component {
    private final Level level;
    private Mesh mesh;

    public LevelRenderer(Level level) {
        this.level = level;
    }

    @Override
    public void start() {
        Scene scene = getEntity().getScene();
        LevelGeometry geometry = new LevelGeometry(level,
                AssetPool.getTexture("assets/tiles/ceiling.png").getTexId(),
                AssetPool.getTexture("assets/tiles/wall.png").getTexId(),
                AssetPool.getTexture("assets/tiles/wall.png").getTexId());

        mesh = new Mesh(geometry);
        gameObject.getGame().getRenderer().add(mesh);

        createLights();
        createPlayer();
    }

    @Override
    public void update(double dt) {
        mesh.getModelMatrix().set(gameObject.transform.getMatrix());
        if(Input.keyPressed(GLFW_KEY_P)) {
            GameObject levelObject = getEntity().getScene().getEntityByName("Level");
            getEntity().getGame().playScene("LevelEditorScene", new TestParams(levelObject.getComponent(Level.class, "Level").getPath()));
        }
    }

    @Override
    public void destroy() {
        gameObject.getGame().getRenderer().remove(mesh);
    }

    private void createPlayer() {
        Scene scene = getEntity().getScene();
        LevelMap levelMap = level.getLevelMap();
        levelMap.getEntities().stream()
                .filter(e -> Objects.equals(e.name, "player"))
                .findFirst()
                .ifPresent(playerEntity -> {
                    Vector3d position = new Vector3d(playerEntity.x - levelMap.minX, 2, playerEntity.y - levelMap.minY).mul(Level.TILE_SIZE);
                    GameObject hero = new GameObject(scene, "Hero");
                    hero.transform.position.set(position);
                    hero.addComponent("Controller", new HeroController(40f, 6f));
                    hero.addComponent("CameraFollower", new CameraFollower(scene.getCamera().getEntity(), new Vector3d()));
                    scene.add(hero);
                });
    }

    private void createLights() {
        Scene scene = getEntity().getScene();
        LevelMap levelMap = level.getLevelMap();
        List<Light> lights = levelMap.getLights();
        for (Light light : lights) {
            GameObject go = new GameObject(scene);
            go.transform.position.set(new Vector3d(light.x - levelMap.minX, light.top, light.y - levelMap.minY).mul(Level.TILE_SIZE));
            SpotLight spotLight = new SpotLight(new Color(light.color), light.radiusMin * Level.TILE_SIZE, light.radiusMax * Level.TILE_SIZE);
            go.addComponent("Light", spotLight);
            Sprite sprite = new Sprite(AssetPool.getTexture("assets/tiles/torch.png"));
            sprite.setWidth(1);
            sprite.setHeight(1);
            go.addComponent("Renderer", new SpriteRenderer(sprite, -1));
            scene.add(go);
        }

        GameObject ambientLightGameObject = new GameObject(scene);
        ambientLightGameObject.addComponent("AmbientLight", new AmbientLight(new Color(0.05, 0.05, 0.05)));
        scene.add(ambientLightGameObject);

        GameObject fogGameObject = new GameObject(scene);
        fogGameObject.addComponent("Fog", new Fog(20, 25, Color.BLACK));
        scene.add(fogGameObject);
    }
}
