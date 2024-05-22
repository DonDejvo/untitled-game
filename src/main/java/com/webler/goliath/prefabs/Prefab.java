package com.webler.goliath.prefabs;

import com.webler.goliath.core.GameObject;
import com.webler.goliath.core.Scene;

public interface Prefab {
    /**
    * Creates a GameObject that is part of the scene. This method is called by Scene#create ( java. util. List ) to create a new instance of a GameObject in the scene.
    * 
    * @param scene - The scene to create the GameObject in.
    * 
    * @return The newly created GameObject or null if there was an error creating the GameObject in the scene ( for example if the scene is not valid
    */
    GameObject create(Scene scene);
}
