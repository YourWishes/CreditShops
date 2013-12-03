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
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * @author      Dominic
 * @since       30/10/2013
 */
public class BuyCommand extends BukkitCommand {
    public BuyCommand() {
        super("buy");
        this.addSubCommandOption(SubCommandOption.ITEM_OPTION);
    }
    
    @Override
    public boolean cmd(CommandSender sender, Command cmd, String label, String[] args) {
        if(!isPlayer(sender)) return this.noPermission(sender, cmd, label, args);
        if(args.length < 1) {
            sendMessage(sender, ChatError + "Please enter an item name.");
            return true;
        }
        
        int amt = 1;
        if(args.length > 1) {
            if(isInt(args[args.length -1 ])) {
                amt = getInt(args[args.length - 1]);
            }
        }
        
        if(amt < 1) {
            sendMessage(sender, ChatError + "Please enter an amont more than 0.");
            return true;
        }
        
        DomsItem item = null;
        String[] cards = args;
        if(cards.length > 1) {
            if(isInt(cards[cards.length - 1])) {
                cards = new String[args.length - 1];
                for(int i = 0; i < args.length - 1; i++) {
                    cards[i] = args[i];
                }
            }
        }
        if(args.length > 0) {
            try {
                item = DomsItem.guessItem(Base.arrayToString(cards, " "));
                item.getName();
            } catch (InvalidItemException ex) {
                sendMessage(sender, ChatError + "This is an invalid item.");
                return true;
            }
        }
        
        //Determine if this item can be purchased
        if(!ItemPricer.isBuyable(item)) {
            sendMessage(sender, ChatError + "Can't buy this.");
            return true;
        }
        
        double cost = ItemPricer.getPrice(item) * ((double) amt) * getConfig().getDouble("cost.command.buy.inflateprice", 1.00d);
        String v = Base.formatEcon(cost);
        if(cost > Base.getBalance(sender.getName()) && useEcon()) {
            sendMessage(sender, ChatError + "You need " + v + " for that.");
            return true;
        }
        
        //Buy
        
        List<DomsItem> items = DomsItem.multiply(item, amt);
        for(DomsItem i : items) {
            try {i.giveToPlayer(getPlayer(sender));} catch(InvalidItemException e) {return true;}
        }
        Base.chargePlayer(sender.getName(), cost);
        
        sendMessage(sender, "Purchased " + ChatImportant + 
                Base.listToString(DomsItem.getHumanMessages(items)).replaceAll(ChatDefault, ChatImportant)
                + ChatDefault + " for " + ChatImportant + v);
        return true;
    }
}
