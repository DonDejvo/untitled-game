package com.webler.goliath.dialogs;

import com.webler.goliath.dialogs.nodes.DialogNode;
import lombok.Getter;

@Getter
public class DialogOption {
    private boolean repeat;
    private String dialogName;
    private DialogNode node;

    public DialogOption(String dialogName, boolean repeat, DialogNode node) {
        this.dialogName = dialogName;
        this.repeat = repeat;
        this.node = node;
    }

}
