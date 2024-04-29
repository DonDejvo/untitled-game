package com.webler.untitledgame;

import com.webler.goliath.core.Component;

public class CubeController extends Component {
    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        getEntity().transform.rotation.rotateX(dt);
        getEntity().transform.rotation.rotateY(dt * -0.5);
    }

    @Override
    public void destroy() {

    }
}
