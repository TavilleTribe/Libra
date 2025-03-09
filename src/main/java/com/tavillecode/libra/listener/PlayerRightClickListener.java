package com.tavillecode.libra.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * @author Interface39
 * @version 1.0
 * @description: TODO
 * @date 2025/3/6 21:58
 */
public class PlayerRightClickListener implements Listener {
     @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent e) {
         if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
         }
     }
}
