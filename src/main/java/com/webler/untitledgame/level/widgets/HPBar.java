package com.webler.untitledgame.level.widgets;

import com.webler.goliath.graphics.Color;
import com.webler.goliath.graphics.canvas.Canvas;
import com.webler.goliath.math.MathUtils;

public record HPBar(int hp, int maxHP) {

    public void draw(Canvas canvas, float x, float y, float width) {

        float height = width * 0.08f;

        canvas.setColor(new Color(0.5, 0.5, 0.5, 0.5));
        canvas.rect(x, y, width, height);

        canvas.setColor(Color.RED);
        canvas.rect(x, y, width * (float) MathUtils.clamp((float)hp / maxHP, 0, 1), height);

        canvas.setColor(Color.BLACK);
        canvas.rect(x, y, width, 3);
        canvas.rect(x, y + height, width, 3);
        canvas.rect(x, y, 3, height);
        canvas.rect(x + width, y, 3, height);
    }

}
