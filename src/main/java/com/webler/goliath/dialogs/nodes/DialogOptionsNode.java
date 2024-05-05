package com.webler.goliath.dialogs.nodes;

import com.webler.goliath.dialogs.DialogOption;

import java.util.Arrays;

public class DialogOptionsNode extends DialogNode {
    private DialogOption[] options;
    private int selectedIdx;

    public DialogOptionsNode(DialogOption[] options) {
        this.options = options;
        this.selectedIdx = 0;
    }

    public DialogOption[] getOptions() {
        return options;
    }

    @Override
    public DialogNodeType getType() {
        return DialogNodeType.OPTIONS;
    }

    @Override
    public DialogNode getNext() {
        return options[selectedIdx].getNode();
    }

    public void selectOption(int idx) {
        selectedIdx = idx;
    }
}
