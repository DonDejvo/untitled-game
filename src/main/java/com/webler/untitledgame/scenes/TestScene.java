package com.webler.untitledgame.scenes;

import com.webler.goliath.Game;
import com.webler.goliath.audio.AudioManager;
import com.webler.goliath.audio.Sound;
import com.webler.goliath.audio.SoundSource;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;
import com.webler.goliath.core.SceneParams;
import com.webler.goliath.prefabs.PerspectiveCameraPrefab;
import com.webler.goliath.utils.AssetPool;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.AL_LINEAR_DISTANCE_CLAMPED;

public class TestScene extends Scene {
    public TestScene(Game game) {
        super(game);
    }

    @Override
    public void init(SceneParams params) {
        System.out.println("TestScene init called");

        GameObject camera = new PerspectiveCameraPrefab(Math.PI / 3, 0.1, 100).create(this);
        add(camera);


        Sound gunSound = AssetPool.getSound("untitled-game/sounds/gun.ogg");

        try {
            for (int i = 0; i < 10; i++) {
                AudioManager.play(gunSound.getBufferId());

                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
