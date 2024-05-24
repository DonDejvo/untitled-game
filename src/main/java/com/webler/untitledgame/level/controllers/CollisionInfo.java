package com.webler.untitledgame.level.controllers;

import com.webler.goliath.core.GameObject;
import org.joml.Vector3d;

public record CollisionInfo(Vector3d position, Vector3d direction, GameObject collidingObject) {
}
