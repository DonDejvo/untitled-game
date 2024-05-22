package com.webler.goliath.dialogs.nodes;

import lombok.Getter;

public class DialogTextNode extends DialogNode {
    private final DialogNode next;
    @Getter
    private final String dialogName;

    public DialogTextNode(String dialogName, DialogNode next) {
        this.dialogName = dialogName;
        this.next = next;
    }

    /**
    * Returns the next node in the list. This is used to determine if there are more nodes to be displayed to the user.
    * 
    * 
    * @return The next DialogNode in the list or null if there are no more nodes to be displayed to the
    */
    @Override
    public DialogNode getNext() {
        return next;
    }

    /**
    * Returns the type of the node. In this case DialogNodeType#TEXT is returned. This can be used to determine whether or not the node is a text node.
    * 
    * 
    * @return dialog node type ( text or not ) or null if it is not a text node ( default )
    */
    @Override
    public DialogNodeType getType() {
        return DialogNodeType.TEXT;
    }

}
