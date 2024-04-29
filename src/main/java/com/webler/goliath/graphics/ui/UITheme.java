package com.webler.goliath.graphics.ui;

import com.webler.goliath.graphics.Color;
import org.joml.Vector2f;

public record UITheme(Color backgroundColor,
                      Color textColor,
                      Color buttonColor,
                      Color buttonHoverColor,
                      Color buttonTextColor,
                      Vector2f padding,
                      float fontSize) {
}
