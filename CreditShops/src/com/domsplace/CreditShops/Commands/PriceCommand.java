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
import com.domsplace.CreditShops.Exceptions.InvalidItemException;
import com.domsplace.CreditShops.Objects.DomsItem;
import com.domsplace.CreditShops.Objects.ItemPricer;
import com.domsplace.CreditShops.Objects.SubCommandOption;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class PriceCommand extends BukkitCommand {
    public PriceCommand() {
        super("price");
        this.addSubCommandOption(SubCommandOption.ITEM_OPTION);
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1 && !isPlayer(sender)) {
            sendMessage(sender, ChatError + "Please enter an item name.");
            return true;
        }
        
        List<DomsItem> items;
        if(args.length > 0) {
            try {
                items = DomsItem.guessItems(Base.arrayToString(args, " "));
            } catch (InvalidItemException ex) {
                sendMessage(sender, ChatError + "This is an invalid item.");
                return true;
            }
        } else {
            items = DomsItem.itemStackToDomsItems(getPlayer(sender).getItemInHand());
            if(items == null || items.size() < 1 || items.get(0).isAir()) {
                sendMessage(sender, ChatError + "This is an invalid item.");
                return true;
            }
        }
        
        DomsItem item = items.get(0);
        if(item == null) {
            sendMessage(sender, ChatError + "This is an invalid item.");
            return true;
        }
        
        double worth = ItemPricer.getPrice(item);
        
        List<String> msgs = new ArrayList<String>();
        msgs.add(ChatImportant + "Value of " + Base.listToString(DomsItem.getHumanMessages(items)).replaceAll(ChatDefault, ChatImportant));
        msgs.add(ChatImportant + "Worth Per Item: " + ChatDefault + Base.formatEcon(worth));
        msgs.add(ChatImportant + "Total Value: " + ChatDefault + Base.formatEcon(worth * ((double) items.size())));
        msgs.add(ChatImportant + "Sell Worth: " + ChatDefault + Base.formatEcon(worth * ((double) items.size()) * getConfig().getDouble("cost.command.sell.deflateprice", 1.00d)));
        msgs.add(ChatImportant + "Buy Worth: " + ChatDefault + Base.formatEcon(worth * ((double) items.size()) * getConfig().getDouble("cost.command.buy.inflateprice", 1.00d)));
        
        sendMessage(sender, msgs);
        return true;
    }
}
