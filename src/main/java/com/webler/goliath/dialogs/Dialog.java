package com.webler.goliath.dialogs;

public class Dialog {
    private String text;
    private String title;

    public Dialog(String text, String title) {
        this.text = text;
        this.title = title;
    }

    public Dialog(String text) {
        this.text = text;
        this.title = "";
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}
