/*
 * Copyright 2013 Dominic.
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

package com.domsplace.CreditShops.Commands;

import com.domsplace.CreditShops.Bases.Base;
import com.domsplace.CreditShops.Bases.BukkitCommand;
import com.domsplace.CreditShops.Objects.DomsItem;
import com.domsplace.CreditShops.Objects.ItemPricer;
import com.domsplace.CreditShops.Objects.SubCommandOption;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class SellCommand extends BukkitCommand {
    public SellCommand() {
        super("sell");
        this.addSubCommandOption(SubCommandOption.ITEM_OPTION);
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) return this.noPermission(sender, cmd, label, args);
        
        DomsItem held = DomsItem.createItem(getPlayer(sender).getItemInHand());
        if(held == null || held.isAir()) {
            sendMessage(sender, ChatError + "You must be holding an item.");
            return true;
        }
        
        if(!ItemPricer.isSellable(held)) {
            sendMessage(sender, ChatError + "This item cannot be sold.");
            return true;
        }
        
        int amount = getPlayer(sender).getItemInHand().getAmount();
        if(args.length > 0) {
            if(!isInt(args[0]) || getInt(args[0]) < 1) {
                sendMessage(sender, ChatError + "Amount must be a number above 0");
                return true;
            }
            
            amount = getInt(args[0]);
        }
        
        //determine worth
        double sellWorth = ItemPricer.getPrice(held);
        sellWorth = sellWorth * ((double) amount);
        sellWorth = sellWorth * getConfig().getDouble("cost.command.sell.deflateprice", 1.00d);
        String v = Base.formatEcon(sellWorth);
        
        //Take Items
        if(!DomsItem.hasItem(held, amount, getPlayer(sender).getInventory())) {
            sendMessage(sender, ChatError + "You don't have these items.");
            return true;
        }
        
        DomsItem.removeItem(held, amount, getPlayer(sender).getInventory());
        
        if(Base.useEcon()) {
            Base.chargePlayer(sender.getName(), -sellWorth);
        }
        sendMessage(sender, 
            "Sold " + ChatImportant + 
            Base.listToString(DomsItem.getHumanMessages(DomsItem.multiply(held, amount))).replaceAll(ChatDefault, ChatImportant)
            + ChatDefault + " for " + ChatImportant + v
        );
        return true;
    }
}
