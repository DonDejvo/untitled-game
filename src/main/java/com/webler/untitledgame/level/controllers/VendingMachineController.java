package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.dialogs.Dialog;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.components.DialogManager;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.components.LevelItem;
import com.webler.untitledgame.components.PathFinder;
import com.webler.untitledgame.level.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class VendingMachineController extends NpcController {
    private List<LevelItem> sellingItems;

    public VendingMachineController(Level level, BoxCollider3D collider, DialogComponent dialogComponent, PathFinder pathFinder) {
        super(level, collider, dialogComponent, pathFinder, 0);
        sellingItems = new ArrayList<>();
    }

    @Override
    public void start() {
        super.start();

        sellingItems.add((LevelItem) level.getRegisteredObject("caffe_latte"));
        sellingItems.add((LevelItem) level.getRegisteredObject("caffe_mocha"));
        sellingItems.add((LevelItem) level.getRegisteredObject("cappuccino"));

        DialogManager dialogManager = level.getComponent(DialogManager.class, "DialogManager");

        for(LevelItem item : sellingItems) {
            dialogManager.addDialog("vm__sell_" + item.getIdentifier(),
                    new Dialog("*Sold one " + item.getName() + "*", "Vending Machine"));
            dialogManager.addDialog("vm__buy_" + item.getIdentifier(),
                    new Dialog(item.getName() + " (" + item.getPrice() + " gold)"));
        }
        dialogManager.addDialog("vm__no_gold", new Dialog("I don't have enough gold."));
    }

    @EventHandler
    public void onDialogNext(DialogNextEvent event) {
        if(event.getGameObject() != gameObject) return;

        PlayerController playerController = level.getPlayer().getComponent(PlayerController.class, "Controller");

        for(LevelItem item : sellingItems) {
            if(event.getDialogName().equals("vm__sell_" + item.getIdentifier())) {
                playerController.buy(item.getIdentifier(), item.getPrice());
            }
        }

        initDialogs();
    }

    @Override
    protected void initDialogs() {
        GameObject player = level.getPlayer();
        Inventory inventory = player.getComponent(Inventory.class, "Inventory");

        dialogComponent.getOptions().clear();

        for(LevelItem item : sellingItems) {

            dialogComponent.addOption(new DialogOption("vm__buy_" + item.getIdentifier(), true,
                    new DialogTextNode(inventory.getItemCount("gold") >= item.getPrice() ?
                            "vm__sell_" + item.getIdentifier() : "vm__no_gold", null)));
        }
    }
}
