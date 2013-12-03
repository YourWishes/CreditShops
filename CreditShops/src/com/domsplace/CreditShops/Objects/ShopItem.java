/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import static com.domsplace.CreditShops.Bases.Base.ChatDefault;
import static com.domsplace.CreditShops.Bases.Base.ChatImportant;
import java.util.ArrayList;
import org.bukkit.ChatColor;

/**
 *
 * @author Dominic Masters
 */
public abstract class ShopItem extends ShopButton {
    private int stock;
    private boolean forSale;
    
    public ShopItem(DomsInventoryGUI gui, DomsItem icon, Shop shop, int stock) {
        super(gui, icon, shop);
        this.stock = stock;
        this.forSale = (this instanceof BuyableItem);
    }
    
    public int getStock() {return this.stock;}
    public void setStock(int stock) {this.stock = stock; this.getShop().update();}
    
    public void update() {
        this.forSale = (this instanceof BuyableItem);
        DomsItem icon = this.getIcon().copy();
        icon.setLores(new ArrayList<String>());
        if(this.stock > 0) {
            icon.addLore(ChatImportant + this.stock + ChatDefault + " remaining.");
            if(Base.useEcon()) {
                if(this.forSale) {
                    icon.addLore(ChatImportant + "Cost: " + ChatColor.GREEN + Base.formatEcon(this.getShop().getSellingPrice(icon)));
                } else {
                    icon.addLore(ChatImportant + "Worth: " + ChatColor.GREEN + Base.formatEcon(this.getShop().getBuyingPrice(icon)));
                }
            }
        } else {
            icon.addLore("" + ChatColor.DARK_RED + ChatColor.BOLD + "No Stock Remaining.");
            this.remove();
        }
        this.setIcon(icon, this.getStock());
    }
    
    @Override
    public void remove() {
        super.remove();
        this.getShop().removeItemForSaleNoUpdate(this);
        this.getShop().removeItemForSellingNoUpdate(this);
    }
}
