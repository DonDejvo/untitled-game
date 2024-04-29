package com.webler.untitledgame.components;

import com.webler.goliath.core.Component;

public class MaxwellRotor extends Component {
    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {
        getEntity().transform.rotation.rotateY(dt);
        getEntity().transform.rotation.rotateX(-dt * 1.5);
    }

    @Override
    public void destroy() {

    }
}
