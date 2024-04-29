package com.webler.untitledgame.level.dialogs;

public class DialogNode {
    private Dialog dialog;
    private DialogNode next;

    public DialogNode(Dialog dialog, DialogNode next) {
        this.dialog = dialog;
        this.next = next;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public DialogNode getNext() {
        return next;
    }
}