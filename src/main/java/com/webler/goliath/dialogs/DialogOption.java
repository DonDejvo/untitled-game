package com.webler.goliath.dialogs;

public class DialogOption {
    private boolean repeat;
    private String text;
    private DialogNode node;

    public DialogOption(boolean repeat, String text, DialogNode node) {
        this.repeat = repeat;
        this.text = text;
        this.node = node;
    }

    public String getText() {
        return text;
    }

    public DialogNode getNode() {
        return node;
    }

    public boolean isRepeat() {
        return repeat;
    }
}
