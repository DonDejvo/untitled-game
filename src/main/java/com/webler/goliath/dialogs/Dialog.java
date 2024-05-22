package com.webler.goliath.dialogs;

import lombok.Getter;

@Getter
public class Dialog {
    private final String text;
    private final String title;

    public Dialog(String text, String title) {
        this.text = text;
        this.title = title;
    }

    public Dialog(String text) {
        this.text = text;
        this.title = "";
    }

}
