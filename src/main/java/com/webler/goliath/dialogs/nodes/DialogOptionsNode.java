package com.webler.goliath.dialogs.nodes;

import com.webler.goliath.dialogs.DialogOption;
import lombok.Getter;

public class DialogOptionsNode extends DialogNode {
    @Getter
    private final DialogOption[] options;
    private int selectedIdx;

    public DialogOptionsNode(DialogOption[] options) {
        this.options = options;
        this.selectedIdx = 0;
    }

    /**
    * Returns the type of dialog node. By default this is DialogNodeType#OPTIONS. Subclasses may override this method to provide their own dialog node type.
    * 
    * 
    * @return The type of dialog node to be used in the dialog. By default this is DialogNodeType#OPTIONS
    */
    @Override
    public DialogNodeType getType() {
        return DialogNodeType.OPTIONS;
    }

    /**
    * Returns the next node in the dialog. If there are no more nodes returns null. This method is called by #doSelect ( java. lang. Object ) and can be used to navigate to the next node that is selected or to the first node of the dialog ( if there is no next node ).
    * 
    * 
    * @return DialogNode the next node in the dialog or null if there aren't any nodes to navigate to
    */
    @Override
    public DialogNode getNext() {
        return options[selectedIdx].node();
    }

    /**
    * Selects the option at the given index. This is useful for choosing a selection in the list of options that can be selected on the command line.
    * 
    * @param idx - The index of the option to select. If - 1 the option is selected
    */
    public void selectOption(int idx) {
        selectedIdx = idx;
    }
}
