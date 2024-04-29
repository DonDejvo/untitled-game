package com.webler.untitledgame.level.dialogs;

public abstract class DialogOption {
    private String text;
    private boolean repeat;

    public DialogOption(String text, boolean repeat) {
        this.text = text;
        this.repeat = repeat;
    }

    public abstract void begin();

    public abstract void end();
}