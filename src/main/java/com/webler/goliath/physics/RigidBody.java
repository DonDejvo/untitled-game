package com.webler.goliath.physics;

import com.webler.goliath.core.Component;
import com.webler.goliath.physics.colliders.Collider;

public class RigidBody extends Component {
    private Collider collider;

    public RigidBody(Collider collider) {
        this.collider = collider;
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }
}
