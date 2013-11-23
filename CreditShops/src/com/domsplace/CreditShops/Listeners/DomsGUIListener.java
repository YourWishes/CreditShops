/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Listeners;

import com.domsplace.CreditShops.Bases.DomsListener;
import com.domsplace.CreditShops.Objects.DomsGUIButton;
import com.domsplace.CreditShops.Objects.DomsInventoryGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *
 * @author Dominic Masters
 */
public class DomsGUIListener extends DomsListener {
    @EventHandler
    public void blockClicking(InventoryClickEvent e) {
        //Begin Castin
        if(!isPlayer(e.getWhoClicked())) return;
        if(e.getSlot() < 0) return;
        DomsInventoryGUI gui = DomsInventoryGUI.getGUI(e.getInventory());
        if(gui == null) return;
        e.setCancelled(true);
        gui.onClick(getPlayer(e.getWhoClicked()));
        DomsGUIButton button = gui.getButtonFromSlot(e.getSlot());
        if(button == null) return;
        button.onClick(getPlayer(e.getWhoClicked()));
    }
}
