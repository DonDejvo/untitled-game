package com.webler.goliath.dialogs;

public class DialogNode {
    private DialogNode next;
    private String dialogName;

    public DialogNode(String dialogName, DialogNode next) {
        this.dialogName = dialogName;
        this.next = next;
    }

    public DialogNode getNext() {
        return next;
    }

    public String getDialogName() {
        return dialogName;
    }
}
