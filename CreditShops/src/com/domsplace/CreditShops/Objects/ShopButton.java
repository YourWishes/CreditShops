/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.domsplace.CreditShops.Objects;

/**
 *
 * @author Dominic Masters
 */
public abstract class ShopButton extends DomsGUIButton {
    private Shop shop;
    
    public ShopButton(DomsInventoryGUI gui, DomsItem icon, Shop shop) {
        super(gui, icon);
        this.shop = shop;
    }
    
    public Shop getShop() {return this.shop;}
}
