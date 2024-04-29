package com.webler.untitledgame.level.controllers;

public abstract class EntityController extends Controller {
    protected int hitpoints;

    public EntityController(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public int getHitpoints() {
        return hitpoints;
    }
}
