/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.DataManagers.CraftBukkitManager;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Dominic Masters
 */
public class DomsInventoryGUI {
    public static final int MAX_NAME_LENGTH = 32;
    public static final String DEFAULT_TITLE = "Inventory";
    private static final List<DomsInventoryGUI> INVENTORIES = new ArrayList<DomsInventoryGUI>();
    
    public static List<DomsInventoryGUI> getRegisteredGUIs() {
        return new ArrayList<DomsInventoryGUI>(INVENTORIES);
    }
    
    public static DomsInventoryGUI getGUI(Inventory inv) {
        for(DomsInventoryGUI gui : INVENTORIES) {
            if(gui.compare(inv)) return gui;
        }
        return null;
    }
    
    public enum SIZE {
        SIZE_9 (9),
        SIZE_18 (18),
        SIZE_27 (27),
        SIZE_36 (36),
        SIZE_45 (45),
        SIZE_54 (54)
        ;
        
        private int slots;
        SIZE(int slots) {this.slots = slots;}
        public int getSlots() {return this.slots;}
    }
    
    private SIZE size;
    private Inventory inventory;
    private List<DomsGUIButton> buttons;
    
    public DomsInventoryGUI(SIZE size) {
        this.size = size;
        this.buttons = new ArrayList<DomsGUIButton>();
        this.update();
        this.register();
    }
    
    public List<HumanEntity> getViewers() {try {return new ArrayList<HumanEntity>(this.inventory.getViewers());} catch(Exception e) {return new ArrayList<HumanEntity>();}}
    public String getName() {return this.inventory.getTitle();}
    public SIZE getSize() {return this.size;}
    public Inventory getInventory() {return this.inventory;}
    public List<DomsGUIButton> getButtons() {return new ArrayList<DomsGUIButton>(this.buttons);}
    public DomsGUIButton getButtonFromSlot(int slot) {
        if(slot >= buttons.size() || slot < 0) return null;
        return buttons.get(slot);
    }
    
    public void addButton(DomsGUIButton button) {this.buttons.add(button); this.update();}
    
    public void removeButton(DomsGUIButton button) {this.buttons.remove(button); this.update();}
    
    public boolean isFull() {return this.buttons.size() >= this.size.getSlots();}
    
    public void setName(String name) {this.updateName(name);}
    public void setSize(SIZE size) {
        this.size = size;
        this.update();
    }
    public void setViewers(List<HumanEntity> newViewers) {
        for(HumanEntity v : this.getViewers()) {
            if(v == null) continue;
            v.closeInventory();
        }
        
        for(HumanEntity e : newViewers) {
            if(e == null) continue;
            e.openInventory(this.inventory);
        }
    }
    
    public final void register() {INVENTORIES.add(this);}
    public final void deregister() {INVENTORIES.remove(this);}
    
    public List<HumanEntity> closeKeepViewers() {
        List<HumanEntity> viewers = this.getViewers();
        this.close();
        return viewers;
    }
    
    public void close() {
        for(HumanEntity v : this.getViewers()) {
            if(v == null) continue;
            v.closeInventory();
        }
        try {
            this.inventory.clear();
            this.inventory = null;
        } catch(Exception e) {}
    }
    
    public final void update() {
        String name = DEFAULT_TITLE;
        if(this.inventory != null) {
            name = this.getName();
            if(name == null) name = DEFAULT_TITLE;
        }
        this.updateName(name);
    }
    
    private void updateName(String name) {
        List<HumanEntity> old = this.closeKeepViewers();
        name = Base.trim(name, MAX_NAME_LENGTH);
        this.inventory = Bukkit.createInventory(null, this.size.getSlots(), name);
        this.setViewers(old);
        
        for(DomsGUIButton button : this.buttons) {
            try {
                this.inventory.addItem(button.getIcon().getItemStack(button.getIconSize()));
            } catch (InvalidItemException ex) {}
        }
    }
    
    public boolean compare(Inventory inventory) {
        try {
            //Cast e.getInventory to "CraftInventory"
            Class CraftInventory = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getCraftClass("inventory.CraftInventory");
            Object inv = CraftInventory.cast(inventory);
            Object myInventory = CraftInventory.cast(this.inventory);
            
            Class IInventory = CraftBukkitManager.CRAFT_BUKKIT_MANAGER.getMineClass("IInventory");
            Object iInv = CraftInventory.getDeclaredMethod("getInventory").invoke(inv);
            Object iMyInv = CraftInventory.getDeclaredMethod("getInventory").invoke(myInventory);
            
            Inventory getInventoryResult = (Inventory) inventory;
            Inventory myInvResult = (Inventory) myInventory;
            
            return IInventory.cast(iInv).toString().equals(IInventory.cast(iMyInv).toString());
        } catch(Exception ex) {
            return false;
        }
    }
    
    public void onClick(Player clicker) {
    }
}