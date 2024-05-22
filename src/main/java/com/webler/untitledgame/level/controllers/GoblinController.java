package com.webler.untitledgame.level.controllers;

import com.webler.goliath.animation.components.Animator;
import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.utils.AssetPool;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.PathFinder;

public class GoblinController extends EnemyController {
    public GoblinController(Level level, BoxCollider3D collider, PathFinder pathFinder) {
        super(level, collider, pathFinder, 100);
    }

    /**
    * Updates the knight. Called every frame. Overridden to play animations based on acceleration. This is used for moving the knight to an opportunity to stop it if it is on ground.
    * 
    * @param dt - Time since the last update in seconds ( ignored
    */
    @Override
    public void update(double dt) {
        super.update(dt);

        Animator animator = getComponent(Animator.class, "Animator");
        // If the acceleration is negative or not onGround then play the game animation.
        if(Math.abs(acceleration.x) + Math.abs(acceleration.z) == 0 || !onGround) {
            animator.playAnimIfNotPlaying(AssetPool.getAnimation("untitled-game/animations/knight__idle"), true);
        } else {
            animator.playAnimIfNotPlaying(AssetPool.getAnimation("untitled-game/animations/knight__run"), true);
        }
    }
}
