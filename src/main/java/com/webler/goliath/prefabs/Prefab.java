package com.webler.goliath.prefabs;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;

public interface Prefab {
    GameObject create(Scene scene);
}
