package com.webler.untitledgame.level.controllers;

import com.webler.goliath.colliders.BoxCollider3D;
import com.webler.goliath.core.GameObject;
import com.webler.goliath.dialogs.DialogOption;
import com.webler.goliath.dialogs.components.DialogComponent;
import com.webler.goliath.dialogs.events.DialogNextEvent;
import com.webler.goliath.dialogs.nodes.DialogTextNode;
import com.webler.goliath.eventsystem.listeners.EventHandler;
import com.webler.untitledgame.components.Level;
import com.webler.untitledgame.level.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class VendingMachineController extends NpcController {
    private Map<String, Integer> sellingItems;

    public VendingMachineController(Level level, BoxCollider3D collider, DialogComponent dialogComponent) {
        super(level, collider, dialogComponent);
        sellingItems = new HashMap<>();
    }

    public void registerSellingItem(String item, int price) {
        sellingItems.put(item, price);
    }

    @Override
    public void start() {
        super.start();

        registerSellingItem("caffe_latte", 1);
        registerSellingItem("espresso", 2);
        registerSellingItem("americano", 3);
    }

    @EventHandler
    public void onDialogNext(DialogNextEvent event) {
        PlayerController playerController = level.getPlayer().getComponent(PlayerController.class, "Controller");

        for(String item : sellingItems.keySet()) {
            if(event.getDialogName().equals("vm__sell_" + item)) {
                playerController.buy(item, sellingItems.get(item));
            }
        }

        initDialogs();
    }

    @Override
    protected void initDialogs() {
        GameObject player = level.getPlayer();
        Inventory inventory = player.getComponent(Inventory.class, "Inventory");

        dialogComponent.getOptions().clear();

        for(Map.Entry<String, Integer> entry : sellingItems.entrySet()) {
            String item = entry.getKey();
            int price = entry.getValue();

            dialogComponent.addOption(new DialogOption("vm__buy_" + item, true,
                    new DialogTextNode(inventory.getItemCount("gold") >= price ?
                            "vm__sell_" + item : "vm__no_gold", null)));
        }
    }
}
