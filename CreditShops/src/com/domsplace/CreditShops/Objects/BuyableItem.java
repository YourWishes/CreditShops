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
public class BuyableItem extends ShopItem {
    public BuyableItem(Shop shop, DomsItem item, int amt) {
        super(shop.getBuy(), item, shop, amt);
    }

    @Override
    public void onClick(Player clicker) {
        double singleCost = ItemPricer.getPrice(this.getIcon());
        int purchasing = 1; //May change
        double cost = singleCost * (double) purchasing;
        
        if(Base.useEcon()) {
            double balance = Base.getBalance(clicker.getName());
            if(balance < cost) purchasing = 0;
        }
        
        if(purchasing <= 0) {
            Base.sendMessage(clicker, Base.ChatError + "You don't have enough. You need " + Base.formatEcon(cost) + "!");
            return;
        }
        
        DomsItem item = this.getIcon().copy();
        item.setLores(new ArrayList<String>());
        item.setName(null);
        
        if(Base.useEcon()) {
            Base.chargePlayer(clicker.getName(), cost);
            Base.chargePlayer(this.getShop().getOwner(), -cost);
        }
        
        Base.sendMessage(clicker, "Purchased " + Base.ChatImportant + purchasing + " " + item.toHumanString().replaceAll(Base.ChatDefault, Base.ChatImportant));
        try {for(int i = 0; i < purchasing; i++) {
            item.giveToPlayer(clicker);
        }} catch(InvalidItemException e) {}
            
        Base.sendMessage(this.getShop().getOwner(), clicker.getDisplayName() + Base.ChatDefault + 
                " just purchased " + Base.ChatImportant + purchasing + " " + item.toHumanString().replaceAll(Base.ChatDefault, Base.ChatImportant));
        
        this.setStock(this.getStock() - purchasing);
    }
}
