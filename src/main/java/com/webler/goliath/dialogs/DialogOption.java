package com.webler.goliath.dialogs;

import com.webler.goliath.dialogs.nodes.DialogNode;

public record DialogOption(String dialogName, boolean repeat, DialogNode node) {

}
