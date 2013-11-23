/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public abstract class DomsGUIButton extends Base {
    private DomsInventoryGUI gui;
    private DomsItem icon;
    private int iconSize;
    
    public DomsGUIButton(DomsInventoryGUI gui, DomsItem icon) {this(gui, icon, 1);}
    public DomsGUIButton(DomsInventoryGUI gui, DomsItem icon, int iconSize) {
        this.gui = gui;
        this.icon = icon;
        this.iconSize = iconSize;
        
        this.gui.addButton(this);
    }
    
    public DomsItem getIcon() {return this.icon;}
    public DomsInventoryGUI getGUI() {return this.gui;}
    public int getIconSize() {return this.iconSize;}
    
    public void setIcon(DomsItem item, int size) {this.icon = item; this.iconSize = size; this.gui.update();}
    public void setIcon(DomsItem item) {this.icon = item; this.gui.update();}
    public void setIconSize(int amt) {this.iconSize = amt; this.gui.update();}
    
    public abstract void onClick(Player clicker);
    
    public void remove() {
        gui.removeButton(this);
    }
}
