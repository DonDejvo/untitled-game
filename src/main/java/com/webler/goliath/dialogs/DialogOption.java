package com.webler.goliath.dialogs;

import com.webler.goliath.dialogs.nodes.DialogNode;

public class DialogOption {
    private boolean repeat;
    private String dialogName;
    private DialogNode node;

    public DialogOption(String dialogName, boolean repeat, DialogNode node) {
        this.dialogName = dialogName;
        this.repeat = repeat;
        this.node = node;
    }

    public String getDialogName() {
        return dialogName;
    }

    public DialogNode getNode() {
        return node;
    }

    public boolean isRepeat() {
        return repeat;
    }
}
