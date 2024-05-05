package com.webler.goliath.dialogs.nodes;

public class DialogTextNode extends DialogNode {

    private DialogNode next;
    private String dialogName;

    public DialogTextNode(String dialogName, DialogNode next) {
        this.dialogName = dialogName;
        this.next = next;
    }

    @Override
    public DialogNode getNext() {
        return next;
    }

    @Override
    public DialogNodeType getType() {
        return DialogNodeType.TEXT;
    }

    public String getDialogName() {
        return dialogName;
    }
}
