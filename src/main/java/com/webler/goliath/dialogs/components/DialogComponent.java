package com.webler.goliath.dialogs.components;

import com.webler.goliath.core.Component;
import com.webler.goliath.dialogs.DialogOption;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class DialogComponent extends Component {
    private final DialogManager dialogManager;
    @Getter
    private List<DialogOption> options;

    public DialogComponent(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
        options = new ArrayList<>();
    }

    /**
    * Adds an option to the dialog. This is used to set options that are specific to the dialog and should be handled by the Dialog class.
    * 
    * @param option - the option to add to the dialog ( null not permitted
    */
    public void addOption(DialogOption option) {
        options.add(option);
    }

    /**
    * Removes the specified option from the dialog. This is equivalent to calling Dialog#setOption ( DialogOption ) with the option removed.
    * 
    * @param option - the option to remove from the dialog. May not be null
    */
    public void removeOption(DialogOption option) {
        options.remove(option);
    }

    /**
    * Starts the dialog. This is called by the Play button when the user clicks on the play button or presses the OK
    */
    public void play() {
        dialogManager.startDialog(this);
    }

    /**
    * Called when the server is started. This is where we start the web server and the server's state is maintained
    */
    @Override
    public void start() {

    }

    /**
    * Updates the progress bar. This is called every frame to indicate the progress of the animation. The time in seconds since the last call to update () is given by dt
    * 
    * @param dt - the time since the last
    */
    @Override
    public void update(double dt) {

    }

    /**
    * Called when the component is no longer needed. This is the place to do any cleanup that needs to be done
    */
    @Override
    public void destroy() {

    }
}
