package com.webler.untitledgame.components;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.Component;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.Mesh;
import com.webler.goliath.graphics.Sprite;
import com.webler.goliath.graphics.components.Fog;
import com.webler.goliath.graphics.components.MeshRenderer;
import com.webler.goliath.graphics.components.SpriteRenderer;
import com.webler.goliath.graphics.geometry.Cube;
import com.webler.goliath.graphics.light.AmbientLight;
import com.webler.goliath.graphics.light.SpotLight;
import com.webler.goliath.input.Input;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.level.controllers.DoorController;
import com.webler.untitledgame.level.controllers.PlayerController;
import com.webler.untitledgame.level.geometry.LevelGeometry;
import com.webler.untitledgame.level.levelmap.Door;
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
        createDoors();
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
                    double y = level.getBlockTop((int)Math.floor(playerEntity.x - levelMap.minX), (int)Math.floor(playerEntity.y - levelMap.minY));
                    Vector3d position = new Vector3d(playerEntity.x - levelMap.minX, y, playerEntity.y - levelMap.minY).mul(Level.TILE_SIZE);
                    GameObject player = new GameObject(scene);
                    BoxCollider3D collider = new BoxCollider3D(new Vector3d(2, 3, 2));
                    player.addComponent("Collider", collider);
                    PlayerController playerController = new PlayerController(level, scene.getCamera(), collider);
                    player.addComponent("Controller", playerController);
                    position.y += collider.getSize().y / 2;
                    player.transform.position.set(position);
                    scene.add(player);
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

    private void createDoors() {
        Scene scene = getEntity().getScene();
        LevelMap levelMap = level.getLevelMap();
        List<Door> doors = levelMap.getDoors();
        for (Door door : doors) {
            GameObject doorGameObject = new GameObject(scene);
            doorGameObject.tags.add(Door.TAG);
            MeshRenderer renderer = new MeshRenderer(new Cube(AssetPool.getTexture("assets/tiles/door.png").getTexId()));
            renderer.setColor(new Color(0.5, 0.5, 0.5));
            renderer.offset.x = 0.5;
            doorGameObject.addComponent("Renderer", renderer);
            double y = level.getBlockTop((door.x - levelMap.minX), (door.y - levelMap.minY));
            Vector3d position = new Vector3d(door.x - levelMap.minX + 0.5, y + 0.5, door.y - levelMap.minY + 0.5);
            switch (door.direction) {
                case LEFT:
                    position.z += 0.5;
                    break;
                case RIGHT:
                    position.z -= 0.5;
                    break;
                case UP:
                    position.x += 0.5;
                    break;
                case DOWN:
                    position.x -= 0.5;
                    break;
            }
            doorGameObject.transform.position.set(position.mul(Level.TILE_SIZE));
            doorGameObject.transform.scale.set(Level.TILE_SIZE, Level.TILE_SIZE, Level.TILE_SIZE * 0.25);
            BoxCollider3D collider = new BoxCollider3D(new Vector3d(4, 4, 4));
            doorGameObject.addComponent("Collider", collider);
            doorGameObject.addComponent("Controller", new DoorController(level, collider, door.direction));
            scene.add(doorGameObject);
        }
    }
}
