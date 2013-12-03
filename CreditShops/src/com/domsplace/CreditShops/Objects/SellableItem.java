/*
 * Copyright 2013 Dominic Masters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domsplace.CreditShops.Objects;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author Dominic Masters
 */
public class SellableItem extends ShopItem {
    private boolean clicked;
    
    public SellableItem(Shop shop, DomsItem item, int amt) {
        super(shop.getSell(), item, shop, amt);
    }

    @Override
    public void onClick(Player clicker) {
        if(this.getShop().isOwner(clicker)) {
            if(!clicked) {
                Base.sendMessage(clicker, ChatError + "You cannot sell this. Click again to take it out of the store.");
                clicked = true;
                return;
            }
            
            //Remove
            sendMessage(clicker, "Removed item for selling.");
            this.setStock(0);
            return;
        }
        
        double singleWorth = this.getShop().getBuyingPrice(this.getIcon());
        int selling = 1; //May change
        double worth = singleWorth * (double) selling;
        
        if(this.getShop().getOwner() != null && !this.getShop().getOwner().isOnline()) {
            Base.sendMessage(clicker, Base.ChatError + "The store owner must be online.");
            return;
        }
        
        DomsItem item = this.getIcon().copy();
        item.setLores(new ArrayList<String>());
        item.setName(null);
        
        if(!DomsItem.hasItem(item, selling, clicker.getInventory())) {
            Base.sendMessage(clicker, Base.ChatError + "You don't have the needed items.");
            return;
        }
        
        if(Base.useEcon() && this.getShop().getOwner() != null) {
            double balance = Base.getBalance(this.getShop().getOwner().getName());
            if(balance < worth) {
                Base.sendMessage(clicker, Base.ChatError + "The shop owner doesn't have " + Base.formatEcon(worth));
                return;
            }
        }
        
        //Charge Owner, give cash to player and take stuff.
        Base.sendMessage(clicker, "Purchased " + Base.ChatImportant + selling + " " + item.toHumanString().replaceAll(Base.ChatDefault, Base.ChatImportant));
        
        if(Base.useEcon()) {
            Base.chargePlayer(clicker.getName(), -worth);
            Base.chargePlayer(this.getShop().getOwner(), worth);
        }
        
        try {item.giveToPlayer(this.getShop().getOwner().getPlayer());} catch(Exception e) {}
        DomsItem.removeItem(item, selling, clicker.getInventory());
        Base.sendMessage(this.getShop().getOwner(), Base.ChatImportant + 
                clicker.getDisplayName() + Base.ChatDefault + " just sold " + 
                Base.ChatImportant + selling + " " + 
                item.toHumanString().replaceAll(Base.ChatDefault, Base.ChatImportant)
                + Base.ChatDefault + " to you."
        );
        
        this.setStock(this.getStock() - selling);
    }
}
