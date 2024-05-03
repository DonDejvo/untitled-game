package com.webler.goliath.dialogs.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.dialogs.DialogOption;

import java.util.ArrayList;
import java.util.List;

public class DialogComponent extends Component {
    private DialogManager dialogManager;
    private List<DialogOption> options;

    public DialogComponent(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
        options = new ArrayList<>();
    }

    public void addOption(DialogOption option) {
        options.add(option);
    }

    public void removeOption(DialogOption option) {
        options.remove(option);
    }

    public List<DialogOption> getOptions() {
        return options;
    }

    public void play() {
        dialogManager.startDialog(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void destroy() {

    }
}
